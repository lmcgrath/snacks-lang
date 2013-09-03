package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.*;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static snacks.lang.SnacksRuntime.BOOTSTRAP;
import static snacks.lang.ast.Type.isInstantiable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import snacks.lang.ast.*;

public class Compiler implements Generator, Reducer {

    private static final Map<String, String> replacements;

    static {
        replacements = new LinkedHashMap<>();
        replacements.put("?", "$Query");
        replacements.put("!", "$Bang");
        replacements.put("+", "$Plus");
        replacements.put("**", "$Exponent");
        replacements.put("*", "$Multiply");
        replacements.put("/", "$Divide");
        replacements.put("%", "$Modulo");
        replacements.put("unary+", "$Positive");
        replacements.put("unary-", "$Negative");
        replacements.put("unary~", "$BitNot");
        replacements.put("&", "$BitAnd");
        replacements.put("|", "$BitOr");
        replacements.put("^", "$BitXor");
        replacements.put("[]", "$Index");
        replacements.put("...", "$XRange");
        replacements.put("..", "$Range");
        replacements.put("==", "$Equals");
        replacements.put("<<", "$LShift");
        replacements.put(">>>", "$URShift");
        replacements.put(">>", "$RShift");
        replacements.put("<=", "$LessThanEquals");
        replacements.put(">=", "$GreaterThanEquals");
        replacements.put("<", "$LessThan");
        replacements.put(">", "$GreaterThan");
    }

    private final List<JiteClass> acceptedClasses;
    private final Deque<State> states;
    private final Deque<LabelNode> labels;
    private final Deque<ExceptionScope> exceptionScopes;
    private boolean popValue;

    public Compiler() {
        acceptedClasses = new ArrayList<>();
        states = new ArrayDeque<>();
        labels = new ArrayDeque<>();
        exceptionScopes = new ArrayDeque<>();
    }

    public ClassLoader compile(Set<AstNode> declarations) throws CompileException {
        for (AstNode declaration : declarations) {
            generate(declaration);
        }
        SnacksLoader loader = new SnacksLoader(getClass().getClassLoader());
        for (JiteClass jiteClass : acceptedClasses) {
            byte[] bytes = jiteClass.toBytes(JDKVersion.V1_7);
            loader.defineClass(c(jiteClass.getClassName()), bytes);
            writeClass(new File(jiteClass.getClassName() + ".class"), bytes);
        }
        return loader;
    }

    @Override
    public void generateApply(Apply node) {
        generate(node.getFunction());
        generate(node.getArgument());
        block().invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP);
    }

    @Override
    public void generateAssign(Assign node) {
        generate(node.getRight());
        reduce(node.getLeft());
        popValue = false;
    }

    @Override
    public void generateBegin(Begin begin) {
        CodeBlock block = block();
        ExceptionScope scope = currentExceptionScope();
        block.label(scope.getStart());
        generate(begin.getBody());
        block.label(scope.getEnd());
        scope.generateEnsure();
        block.go_to(scope.getExit());
    }

    @Override
    public void generateBooleanConstant(BooleanConstant node) {
        block().ldc(node.getValue());
        block().invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    }

    @Override
    public void generateClosure(Closure node) {
        state().setFields(node.getEnvironment());
        defineClosureFields(node);
        defineClosureConstructor(node);
        generateApply(node.getBody());
    }

    @Override
    public void generateClosureLocator(ClosureLocator locator) {
        CodeBlock block = block();
        String className = javaClass(locator);
        block.newobj(className);
        block.dup();
        for (String variable : locator.getEnvironment()) {
            loadVariable(variable);
        }
        block.invokespecial(className, "<init>", sig(params(void.class, Object.class, locator.getEnvironment().size())));
    }

    @Override
    public void generateDeclarationLocator(DeclarationLocator locator) {
        CodeBlock block = block();
        String className = javaClass(locator);
        block.invokestatic(className, "instance", sig(Object.class));
    }

    @Override
    public void generateDeclaredArgument(DeclaredArgument node) {
        // intentionally empty
    }

    @Override
    public void generateDeclaredExpression(DeclaredExpression node) {
        beginClass(javaClass(node), interfacesFor(node.getType()));
        generate(node.getBody());
        acceptClass();
    }

    @Override
    public void generateDoubleConstant(DoubleConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void generateEmbrace(Embrace node) {
        ExceptionScope scope = currentExceptionScope();
        CodeBlock block = block();
        LabelNode beginCatch = new LabelNode();
        LabelNode endCatch = new LabelNode();
        block.trycatch(scope.getStart(), scope.getEnd(), beginCatch, node.getException().replace('.', '/'));
        block.trycatch(beginCatch, endCatch, scope.getError(), null);
        block.label(beginCatch);
        block.astore(getVariable(node.getVariable()));
        generate(node.getBody());
        block.label(endCatch);
        scope.generateEnsure();
        block.go_to(scope.getExit());
    }

    @Override
    public void generateExceptional(Exceptional node) {
        enterExceptionScope(node);
        generate(node.getBegin());
        for (AstNode embrace : node.getEmbraces()) {
            generate(embrace);
        }
        leaveExceptionScope();
    }

    @Override
    public void generateExpressionConstant(ExpressionConstant node) {
        CodeBlock block = beginBlock();
        LabelNode returnValue = new LabelNode(new Label());
        JiteClass jiteClass = jiteClass();
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, ci(Object.class), null);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.ifnonnull(returnValue);
        generate(node.getValue());
        block.putstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.label(returnValue);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, sig(Object.class), acceptBlock());
    }

    @Override
    public void generateFunction(Function node) {
        defineFunctionInitializer();
        generateApply(node.getBody());
    }

    @Override
    public void generateGuardCase(GuardCase node) {
        CodeBlock block = block();
        LabelNode skipLabel = new LabelNode();
        generate(node.getCondition());
        block().ldc(true);
        block().invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
        block.if_acmpne(skipLabel);
        generate(node.getExpression());
        block.go_to(labels.peek());
        block.label(skipLabel);
    }

    @Override
    public void generateGuardCases(GuardCases node) {
        labels.push(new LabelNode());
        for (AstNode guard : node.getCases()) {
            generate(guard);
        }
        block().label(labels.pop());
    }

    @Override
    public void generateHurl(Hurl hurl) {
        CodeBlock block = block();
        block.invokestatic(p(Errorize.class), "instance", sig(Object.class));
        generate(hurl.getBody());
        block.invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP);
        block.checkcast(p(Throwable.class));
        block.athrow();
    }

    @Override
    public void generateIntegerConstant(IntegerConstant node) {
        CodeBlock block = block();
        block().ldc(node.getValue());
        block.invokestatic(p(Integer.class), "valueOf", sig(Integer.class, int.class));
    }

    @Override
    public void generateReference(Reference node) {
        generate(node.getLocator());
    }

    @Override
    public void generateReferencesEqual(ReferencesEqual node) {
        generate(node.getLeft());
        generate(node.getRight());
        LabelNode skipLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();
        CodeBlock block = block();
        block.if_acmpne(skipLabel);
        block.ldc(true);
        block.go_to(endLabel);
        block.label(skipLabel);
        block.ldc(false);
        block.label(endLabel);
        block.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    }

    @Override
    public void generateResult(Result node) {
        generate(node.getValue());
        block().areturn();
    }

    @Override
    public void generateSequence(Sequence node) {
        List<AstNode> elements = node.getElements();
        for (int i = 0; i < elements.size() - 1; i++) {
            popValue = true;
            generate(elements.get(i));
            if (popValue) {
                block().pop();
            }
        }
        generate(elements.get(elements.size() - 1));
    }

    @Override
    public void generateStringConstant(StringConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void generateSymbol(SymbolConstant node) {
        block().ldc(node.getName());
        block().invokestatic(p(Symbol.class), "valueOf", sig(Symbol.class, String.class));
    }

    @Override
    public void generateVariableDeclaration(VariableDeclaration node) {
        getVariable(node.getName());
        popValue = false;
    }

    @Override
    public void generateVariableLocator(VariableLocator locator) {
        loadVariable(locator.getName());
    }

    @Override
    public void generateVoidApply(VoidApply node) {
        generate(node.getInstantiable());
        block().invokeinterface(p(Invokable.class), "invoke", sig(Object.class));
    }

    @Override
    public void generateVoidFunction(VoidFunction node) {
        defineFunctionInitializer();
        beginBlock();
        generate(node.getBody());
        if (!block().returns()) {
            block().areturn();
        }
        jiteClass().defineMethod("invoke", ACC_PUBLIC, sig(Object.class), acceptBlock());
    }

    @Override
    public void reduceReference(Reference node) {
        reduce(node.getLocator());
    }

    @Override
    public void reduceVariableDeclaration(VariableDeclaration node) {
        block().astore(getVariable(node.getName()));
    }

    @Override
    public void reduceVariableLocator(VariableLocator node) {
        block().astore(getVariable(node.getName()));
    }

    @Override
    public void visitCharacterConstant(CharacterConstant node) {
        block().ldc(node.getValue());
        block().invokestatic(p(Character.class), "valueOf", sig(Character.class, char.class));
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
        JiteClass jiteClass = new JiteClass(name, p(Object.class), interfaces.toArray(new String[interfaces.size()]));
        states.push(new State(jiteClass));
        return jiteClass;
    }

    private CodeBlock block() {
        return state().block();
    }

    private ExceptionScope currentExceptionScope() {
        return exceptionScopes.peek();
    }

    private void defineClosureConstructor(Closure closure) {
        String signature = sig(params(void.class, Object.class, closure.getEnvironment().size()));
        CodeBlock block = beginBlock();
        block.aload(0);
        block.invokespecial(p(Object.class), "<init>", sig(void.class));
        int i = 1;
        for (String field : closure.getEnvironment()) {
            block.aload(0);
            block.aload(i++);
            block.putfield(jiteClass().getClassName(), field, ci(Object.class));
        }
        block.voidreturn();
        jiteClass().defineMethod("<init>", ACC_PUBLIC, signature, acceptBlock());
    }

    private void defineClosureFields(Closure closure) {
        JiteClass jiteClass = jiteClass();
        for (String field : closure.getEnvironment()) {
            jiteClass.defineField(field, ACC_PRIVATE | ACC_FINAL, ci(Object.class), null);
        }
    }

    private void defineFunctionInitializer() {
        JiteClass jiteClass = jiteClass();
        CodeBlock block = beginBlock();
        LabelNode returnValue = new LabelNode(new Label());
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, ci(Object.class), null);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.ifnonnull(returnValue);
        block.newobj(jiteClass.getClassName());
        block.dup();
        block.invokespecial(jiteClass.getClassName(), "<init>", sig(void.class));
        block.putstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.label(returnValue);
        block.getstatic(jiteClass.getClassName(), "instance", ci(Object.class));
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, sig(Object.class), acceptBlock());
        jiteClass.defineDefaultConstructor();
    }

    private void enterExceptionScope(Exceptional node) {
        exceptionScopes.push(new ExceptionScope(node.getEnsure()));
    }

    private void generate(AstNode node) {
        node.generate(this);
    }

    private void generate(Locator locator) {
        locator.generate(this);
    }

    private void generateApply(AstNode body) {
        CodeBlock block = beginBlock();
        generate(body);
        if (!block.returns()) {
            block.areturn();
        }
        jiteClass().defineMethod("apply", ACC_PUBLIC, sig(Object.class, Object.class), acceptBlock());
    }

    private int getVariable(String name) {
        return state().getVariable(name);
    }

    private List<String> interfacesFor(Type type) {
        List<String> interfaces = new ArrayList<>();
        if (isInstantiable(type)) {
            interfaces.add(p(Invokable.class));
        }
        return interfaces;
    }

    private boolean isField(String name) {
        return state().isField(name);
    }

    private boolean isVariable(String name) {
        return state().isVariable(name);
    }

    private String javaClass(DeclaredExpression expression) {
        return javaClass(expression.getModule(), expression.getName());
    }

    private String javaClass(DeclarationLocator locator) {
        return javaClass(locator.getModule(), locator.getName());
    }

    private String javaClass(ClosureLocator locator) {
        return javaClass(locator.getModule(), locator.getName());
    }

    private String javaClass(String module, String name) {
        return module + "/" + javafy(name);
    }

    private String javafy(String name) {
        String javafiedName;
        if ("?".equals(name)) {
            javafiedName = "$Coalesce";
        } else {
            javafiedName = name.substring(0, 1).toUpperCase() + name.substring(1);
            for (String replacement : replacements.keySet()) {
                javafiedName = javafiedName.replace(replacement, replacements.get(replacement));
            }
        }
        if (javafiedName.startsWith("$")) {
            javafiedName = "Op" + javafiedName;
        }
        return javafiedName;
    }

    private JiteClass jiteClass() {
        return state().jiteClass;
    }

    private void leaveExceptionScope() {
        ExceptionScope scope = exceptionScopes.pop();
        block().trycatch(scope.getStart(), scope.getEnd(), scope.getError(), null);
        scope.generateEnsureAll();
        block().label(scope.getExit());
    }

    private void loadVariable(String name) {
        if (isField(name)) {
            block().aload(0);
            block().getfield(jiteClass().getClassName(), name, ci(Object.class));
        } else if (isVariable(name)) {
            block().aload(getVariable(name));
        } else {
            block().aload(1);
        }
    }

    private void reduce(AstNode node) {
        node.reduce(this);
    }

    private void reduce(Locator locator) {
        locator.reduce(this);
    }

    private State state() {
        return states.peek();
    }

    private void writeClass(File file, byte[] bytes) throws CompileException {
        try {
            if (!file.getParentFile().mkdirs() && !file.getParentFile().exists()) {
                throw new IOException("Failed to mkdirs: " + file.getParentFile());
            }
            try (FileOutputStream output = new FileOutputStream(file)) {
                output.write(bytes);
            }
        } catch (IOException exception) {
            throw new CompileException(exception);
        }
    }

    private static final class BlockState {

        private final CodeBlock block;
        private final Map<String, Integer> variables;

        public BlockState(CodeBlock block) {
            this.block = block;
            this.variables = new LinkedHashMap<>();
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
        private List<String> fields;

        public State(JiteClass jiteClass) {
            this.jiteClass = jiteClass;
            this.fields = new ArrayList<>();
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

        public void setFields(Collection<String> fields) {
            this.fields = new ArrayList<>(fields);
        }
    }

    private final class ExceptionScope {

        private final LabelNode start;
        private final LabelNode end;
        private final LabelNode exit;
        private final LabelNode error;
        private final AstNode ensure;

        public ExceptionScope(AstNode ensure) {
            this.ensure = ensure;
            this.start = new LabelNode();
            this.end = new LabelNode();
            this.exit = new LabelNode();
            this.error = new LabelNode();
        }

        public void generateEnsure() {
            if (ensure != null) {
                generate(ensure);
            }
        }

        public void generateEnsureAll() {
            int exceptionVar = getVariable("$snacks$~exception");
            CodeBlock block = block();
            block.label(error);
            block.astore(exceptionVar);
            if (ensure != null) {
                LabelNode ensureLabel = new LabelNode();
                block.trycatch(error, ensureLabel, error, null);
                block.label(ensureLabel);
                generateEnsure();
            }
            block.aload(exceptionVar);
            block.athrow();
        }

        public LabelNode getEnd() {
            return end;
        }

        public AstNode getEnsure() {
            return ensure;
        }

        public LabelNode getError() {
            return error;
        }

        public LabelNode getExit() {
            return exit;
        }

        public LabelNode getStart() {
            return start;
        }
    }
}
