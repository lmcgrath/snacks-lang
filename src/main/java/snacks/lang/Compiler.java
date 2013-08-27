package snacks.lang;

import static java.util.Arrays.asList;
import static me.qmx.jitescript.util.CodegenUtils.*;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static snacks.lang.compiler.ast.Type.isFunction;
import static snacks.lang.compiler.ast.Type.isInstantiable;
import static snacks.lang.compiler.ast.Type.isValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import snacks.lang.compiler.ast.*;

public class Compiler implements AstVisitor {

    private static final Map<String, String> names = new HashMap<>();
    static {
        names.put("*", "Multiply");
        names.put("+", "Plus");
    }
    private final List<JiteClass> acceptedClasses;
    private final Deque<State> states;
    private int closures;
    private boolean popValue;

    public Compiler() {
        acceptedClasses = new ArrayList<>();
        states = new ArrayDeque<>();
        closures = 0;
    }

    public ClassLoader compile(Set<AstNode> declarations) throws CompileException {
        for (AstNode declaration : declarations) {
            compile(declaration);
        }
        SnacksLoader loader = new SnacksLoader(getClass().getClassLoader());
        for (JiteClass jiteClass : acceptedClasses) {
            byte[] bytes = jiteClass.toBytes(JDKVersion.V1_7);
            loader.defineClass(c(jiteClass.getClassName()), bytes);
            try {
                File file = new File(jiteClass.getClassName() + ".class");
                file.getParentFile().mkdirs();
                try (FileOutputStream output = new FileOutputStream(file)) {
                    output.write(bytes);
                }
            } catch (IOException exception) {
                throw new CompileException(exception);
            }
        }
        return loader;
    }

    @Override
    public void visitApply(Apply node) {
        compile(node.getFunction());
        compile(node.getArgument());
        block().invokeinterface(p(Applicable.class), "apply", sig(Object.class, Object.class));
    }

    @Override
    public void visitArgument(Variable node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitBooleanConstant(BooleanConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void visitDeclarationLocator(DeclarationLocator locator) {
        CodeBlock block = block();
        String reference = locator.getModule() + "/" + javafy(locator.getName());
        block.invokestatic(reference, "instance", sig(Object.class));
    }

    @Override
    public void visitDeclaredExpression(DeclaredExpression node) {
        JiteClass jiteClass = beginClass(node.getModule() + "/" + javafy(node.getName()), interfacesFor(node.getType()));
        jiteClass.defineDefaultConstructor();
        defineInstantiator(node, jiteClass);
        if (isFunction(node.getType())) {
            compile(node.getBody());
        }
        acceptClass();
    }

    @Override
    public void visitDoubleConstant(DoubleConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void visitFunction(Function node) {
        CodeBlock block = beginBlock();
        JiteClass parent = jiteClass();
        List<String> fields = getFields();
        if (beginClosure(node.getVariable())) {
            JiteClass closure = jiteClass();
            CodeBlock innerBlock = beginBlock();
            String className = closure.getClassName();
            compile(node.getBody());
            innerBlock.areturn();
            jiteClass().defineMethod("apply", ACC_PUBLIC, sig(Object.class, Object.class), acceptBlock());
            block.newobj(className);
            block.dup();
            for (String field : fields) {
                block.aload(0);
                block.getfield(parent.getClassName(), field, ci(Object.class));
            }
            block.aload(1);
            block.invokespecial(className, "<init>", sig(params(void.class, Object.class, getFields().size())));
            block.areturn();
            acceptClosure();
        } else {
            compile(node.getBody());
            block.areturn();
            jiteClass().defineMethod("apply", ACC_PUBLIC, sig(Object.class, Object.class), acceptBlock());
        }
    }

    @Override
    public void visitIntegerConstant(IntegerConstant node) {
        CodeBlock block = block();
        block().ldc(node.getValue());
        block.invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
    }

    @Override
    public void visitReference(Reference node) {
        compile(node.getLocator());
    }

    @Override
    public void visitResult(Result node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitSequence(Sequence node) {
        List<AstNode> elements = node.getElements();
        for (int i = 0; i < elements.size() - 1; i++) {
            popValue = true;
            compile(elements.get(i));
            if (popValue) {
                block().pop();
            }
        }
        compile(elements.get(elements.size() - 1));
    }

    @Override
    public void visitStringConstant(StringConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration node) {
        compile(node.getValue());
        block().astore(getVariable(node.getName()));
        popValue = false;
    }

    @Override
    public void visitVariableLocator(VariableLocator locator) {
        if (isField(locator.getName())) {
            block().aload(0);
            block().getfield(jiteClass().getClassName(), locator.getName(), ci(Object.class));
        } else if (isVariable(locator.getName())) {
            block().aload(getVariable(locator.getName()));
        } else {
            block().aload(1);
        }
    }

    @Override
    public void visitVoidApply(VoidApply node) {
        compile(node.getInstantiable());
        block().invokeinterface(p(Invokable.class), "invoke", sig(Object.class));
    }

    @Override
    public void visitVoidFunction(VoidFunction node) {
        beginBlock();
        compile(node.getBody());
        block().areturn();
        jiteClass().defineMethod("invoke", ACC_PUBLIC, sig(Object.class), acceptBlock());
    }

    private CodeBlock acceptBlock() {
        return state().acceptBlock();
    }

    private void acceptClass() {
        acceptedClasses.add(states.pop().getJiteClass());
    }

    private boolean acceptClosure() {
        if (inClosure()) {
            acceptClass();
            closures--;
            return true;
        } else {
            return false;
        }
    }

    private CodeBlock beginBlock() {
        return state().beginBlock();
    }

    private JiteClass beginClass(String name, List<String> interfaces) {
        JiteClass jiteClass = new JiteClass(name, p(Object.class), interfaces.toArray(new String[interfaces.size()]));
        states.push(new State(jiteClass));
        return jiteClass;
    }

    private JiteClass beginClass(String name, List<String> interfaces, String variable, List<String> fields) {
        JiteClass jiteClass = new JiteClass(name, p(Object.class), interfaces.toArray(new String[interfaces.size()]));
        states.push(new State(jiteClass, variable, fields));
        return jiteClass;
    }

    private JiteClass beginClass(JiteClass jiteClass) {
        states.push(new State(jiteClass));
        return jiteClass;
    }

    private boolean beginClosure(String variable) {
        if (inClosure()) {
            List<String> fields = extendFields();
            beginClass(generateName(), asList(p(Applicable.class)), variable, fields);
            defineClosureFields(fields);
            defineClosureConstructor(fields);
            closures++;
            return true;
        } else {
            state().setVariable(variable);
            closures++;
            return false;
        }
    }

    private CodeBlock block() {
        return state().block();
    }

    private void compile(AstNode node) {
        node.accept(this);
    }

    private void compile(Locator locator) {
        locator.accept(this);
    }

    private void defineClosureConstructor(final List<String> fields) {
        String signature = sig(params(void.class, Object.class, fields.size()));
        jiteClass().defineMethod("<init>", ACC_PUBLIC, signature, new CodeBlock() {{
            aload(0);
            invokespecial(p(Object.class), "<init>", sig(void.class));
            int i = 1;
            for (String field : fields) {
                aload(0);
                aload(i++);
                putfield(jiteClass().getClassName(), field, ci(Object.class));
            }
            voidreturn();
        }});
    }

    private void defineClosureFields(List<String> fields) {
        JiteClass jiteClass = jiteClass();
        for (String field : fields) {
            jiteClass.defineField(field, ACC_PRIVATE | ACC_FINAL, ci(Object.class), null);
        }
    }

    private void defineInstantiator(DeclaredExpression node, JiteClass jiteClass) {
        CodeBlock block = beginBlock();
        LabelNode returnValue = new LabelNode(new Label());
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, ci(Object.class), null);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.ifnonnull(returnValue);
        if (isValue(node.getType())) {
            compile(node.getBody());
        } else {
            block.newobj(jiteClass.getClassName());
            block.dup();
            block.invokespecial(jiteClass.getClassName(), "<init>", sig(void.class));
        }
        block.putstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.label(returnValue);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, sig(Object.class), acceptBlock());
    }

    private List<String> extendFields() {
        return state().extendFields();
    }

    private String generateName() {
        return jiteClass().getClassName() + "_" + state().nextId();
    }

    private List<String> getFields() {
        return state().getFields();
    }

    private int getVariable(String name) {
        return state().getVariable(name);
    }

    private boolean inClosure() {
        return closures > 0;
    }

    private List<String> interfacesFor(Type type) {
        List<String> interfaces = new ArrayList<>();
        if (isInstantiable(type)) {
            interfaces.add(p(Invokable.class));
        } else if (isFunction(type)) {
            interfaces.add(p(Applicable.class));
        }
        return interfaces;
    }

    private boolean isField(String name) {
        return state().isField(name);
    }

    private boolean isVariable(String name) {
        return state().isVariable(name);
    }

    private String javafy(String name) {
        if (names.containsKey(name)) {
            return names.get(name);
        } else {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
    }

    private JiteClass jiteClass() {
        return state().jiteClass;
    }

    private State state() {
        return states.peek();
    }

    private static final class SnacksLoader extends ClassLoader {

        public SnacksLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(String name, byte[] bytes) {
            return super.defineClass(name, bytes, 0, bytes.length);
        }
    }

    private static final class State {

        private final JiteClass jiteClass;
        private final Deque<BlockState> blocks;
        private final List<String> fields;
        private String variable;
        private int sequence;

        public State(JiteClass jiteClass) {
            this(jiteClass, null, new ArrayList<String>());
        }

        public State(JiteClass jiteClass, String variable, List<String> fields) {
            this.jiteClass = jiteClass;
            this.variable = variable;
            this.fields = new ArrayList<>(fields);
            this.blocks = new ArrayDeque<>();
        }

        public CodeBlock acceptBlock() {
            return blocks.pop().getBlock();
        }

        public CodeBlock beginBlock() {
            blocks.push(new BlockState(new CodeBlock()));
            return block();
        }

        public CodeBlock block() {
            return blocks.peek().getBlock();
        }

        public List<String> extendFields() {
            List<String> fields = new ArrayList<>();
            fields.addAll(this.fields);
            fields.add(variable);
            return fields;
        }

        public List<String> getFields() {
            return fields;
        }

        public JiteClass getJiteClass() {
            return jiteClass;
        }

        public int getVariable(String name) {
            return blocks.peek().getVariable(name);
        }

        public boolean isField(String name) {
            return fields.contains(name);
        }

        public boolean isVariable(String name) {
            return blocks.peek().isVariable(name);
        }

        public int nextId() {
            return sequence++;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }
    }

    private static final class BlockState {

        private final CodeBlock block;
        private final Map<String, Integer> variables;

        public BlockState(CodeBlock block) {
            this.block = block;
            this.variables = new HashMap<>();
        }

        public CodeBlock getBlock() {
            return block;
        }

        public int getVariable(String name) {
            if (!isVariable(name)) {
                variables.put(name, variables.size() + 2);
            }
            return variables.get(name);
        }

        public boolean isVariable(String name) {
            return variables.containsKey(name);
        }
    }
}
