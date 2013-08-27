package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.c;
import static me.qmx.jitescript.util.CodegenUtils.ci;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static snacks.lang.compiler.ast.Type.isFunction;
import static snacks.lang.compiler.ast.Type.isInstantiable;
import static snacks.lang.compiler.ast.Type.isValuable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import snacks.lang.compiler.ast.*;

public class Compiler implements AstVisitor {

    private static final Map<String, String> names = new HashMap<>();

    static {
        names.put("*", "Multiply");
        names.put("+", "Plus");
    }

    private final List<JiteClass> acceptedClasses;
    private final Deque<State> states;

    public Compiler() {
        acceptedClasses = new ArrayList<>();
        states = new ArrayDeque<>();
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
    public void visitDeclaredExpression(final DeclaredExpression node) {
        String className = node.getModule() + "/" + javafy(node.getName());
        JiteClass jiteClass = beginClass(className, interfacesFor(node.getType()));
        CodeBlock block = beginBlock();
        jiteClass.defineDefaultConstructor();
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, ci(Object.class), null);
        block.getstatic(className, "instance", ci(Object.class));
        if (isValuable(node.getType())) {
            compile(node.getBody());
        } else {
            block.newobj(className);
            block.dup();
            block.invokespecial(className, "<init>", sig(void.class));
        }
        block.putstatic(className, "instance", ci(Object.class));
        block.getstatic(className, "instance", ci(Object.class));
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, sig(Object.class), acceptBlock());
        if (!isValuable(node.getType())) {
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
        throw new UnsupportedOperationException(); // TODO
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
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitStringConstant(StringConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitVariableLocator(VariableLocator locator) {
        throw new UnsupportedOperationException(); // TODO
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

    private CodeBlock beginBlock() {
        return state().beginBlock();
    }

    private JiteClass beginClass(String name, List<String> interfaces) {
        return beginClass(new JiteClass(name, p(Object.class), interfaces.toArray(new String[interfaces.size()])));
    }

    private JiteClass beginClass(JiteClass jiteClass) {
        states.push(new State(jiteClass));
        return jiteClass;
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

    private List<String> interfacesFor(Type type) {
        List<String> interfaces = new ArrayList<>();
        if (isInstantiable(type)) {
            interfaces.add(p(Invokable.class));
        } else if (isFunction(type)) {
            interfaces.add(p(Applicable.class));
        }
        return interfaces;
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

    private static final class State {

        private final JiteClass jiteClass;
        private final Deque<CodeBlock> blocks;

        public State(JiteClass jiteClass) {
            this.jiteClass = jiteClass;
            this.blocks = new ArrayDeque<>();
        }

        public CodeBlock acceptBlock() {
            return blocks.pop();
        }

        public CodeBlock beginBlock() {
            blocks.push(new CodeBlock());
            return block();
        }

        public CodeBlock block() {
            return blocks.peek();
        }

        public JiteClass getJiteClass() {
            return jiteClass;
        }
    }

    private static final class SnacksLoader extends ClassLoader {

        public SnacksLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(String name, byte[] bytes) {
            return super.defineClass(name, bytes, 0, bytes.length);
        }
    }
}
