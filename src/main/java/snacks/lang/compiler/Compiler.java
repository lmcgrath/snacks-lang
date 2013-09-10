package snacks.lang.compiler;

import static me.qmx.jitescript.util.CodegenUtils.*;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static snacks.lang.SnacksDispatcher.BOOTSTRAP_APPLY;
import static snacks.lang.SnacksDispatcher.BOOTSTRAP_GET;
import static snacks.lang.Type.isFunction;
import static snacks.lang.Type.isInstantiable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import snacks.lang.*;
import snacks.lang.ast.*;

public class Compiler implements Generator, Reducer {

    private static final Map<String, String> replacements;

    static {
        replacements = new LinkedHashMap<>();
        replacements.put("?", "$Query");
        replacements.put("!", "$Bang");
        replacements.put("+", "$Plus");
        replacements.put("-", "$Dash");
        replacements.put("*", "$Splat");
        replacements.put("/", "$Slash");
        replacements.put("%", "$Frac");
        replacements.put("&", "$Amp");
        replacements.put("|", "$Pipe");
        replacements.put("^", "$Point");
        replacements.put("[]", "$Sammich");
        replacements.put("..", "$Dots");
        replacements.put("...", "$Bore");
        replacements.put("=", "$Equal");
        replacements.put("<", "$Grow");
        replacements.put(">", "$Shrink");
        replacements.put("~", "$Wave");
    }

    private final SnacksLoader loader;
    private final List<JiteClass> acceptedClasses;
    private final Deque<ClassBuilder> builders;
    private Reference currentReference;

    public Compiler(SnacksLoader loader) {
        this.loader = loader;
        this.acceptedClasses = new ArrayList<>();
        this.builders = new ArrayDeque<>();
    }

    public ClassLoader compile(Set<AstNode> declarations) throws CompileException {
        for (AstNode declaration : declarations) {
            generate(declaration);
        }
        for (JiteClass jiteClass : acceptedClasses) {
            byte[] bytes = jiteClass.toBytes(JDKVersion.V1_7);
            loader.defineClass(c(jiteClass.getClassName()), bytes);
            writeClass(new File(jiteClass.getClassName() + ".class"), bytes);
        }
        return loader;
    }

    @Override
    public void generate(AstNode node) {
        node.generate(this);
    }

    @Override
    public void generateAccess(Access node) {
        generate(node.getExpression());
        block().ldc(node.getProperty());
        block().invokedynamic("get", sig(Object.class, Object.class, String.class), BOOTSTRAP_GET);
    }

    @Override
    public void generateApply(Apply node) {
        generate(node.getFunction());
        generate(node.getArgument());
        block().invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP_APPLY);
    }

    @Override
    public void generateAssign(Assign node) {
        generate(node.getRight());
        block().dup();
        node.getLeft().reduce(this);
    }

    @Override
    public void generateBegin(Begin begin) {
        CodeBlock block = block();
        EmbraceScope embrace = currentEmbrace();
        block.label(embrace.getStart());
        generate(begin.getBody());
        block.label(embrace.getEnd());
        embrace.generateEnsure(this);
        block.go_to(embrace.getExit());
    }

    @Override
    public void generateBooleanConstant(BooleanConstant node) {
        block().ldc(node.getValue());
        block().invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
    }

    @Override
    public void generateBreak(Break node) {
        currentLoop().exit();
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
        String className = javaClass(locator.getModule(), locator.getName());
        block.newobj(className);
        block.dup();
        for (String variable : locator.getEnvironment()) {
            loadVariable(variable);
        }
        block.invokespecial(className, "<init>", sig(params(void.class, Object.class, locator.getEnvironment().size())));
    }

    @Override
    public void generateContinue(Continue node) {
        currentLoop().next();
    }

    @Override
    public void generateDeclarationLocator(DeclarationLocator locator) {
        String className = javaClass(locator.getModule(), locator.getName());
        Type type = currentReference.getType();
        if (isFunction(type)) {
            block().invokestatic(className, "instance", "()L" + className + ";");
        } else {
            block().invokestatic(className, "instance", sig(Object.class));
        }
    }

    @Override
    public void generateDeclaredArgument(DeclaredArgument node) {
        // intentionally empty
    }

    @Override
    public void generateDeclaredExpression(DeclaredExpression node) {
        beginClass(javaClass(node.getModule(), node.getName()), interfacesFor(node.getType()));
        generate(node.getBody());
        acceptClass();
    }

    @Override
    public void generateDoubleConstant(DoubleConstant node) {
        block().ldc(node.getValue());
    }

    @Override
    public void generateEmbrace(Embrace node) {
        EmbraceScope scope = currentEmbrace();
        CodeBlock block = block();
        LabelNode beginCatch = new LabelNode();
        LabelNode endCatch = new LabelNode();
        block.trycatch(scope.getStart(), scope.getEnd(), beginCatch, node.getException().replace('.', '/'));
        block.trycatch(beginCatch, endCatch, scope.getError(), null);
        block.label(beginCatch);
        block.astore(getVariable(node.getVariable()));
        generate(node.getBody());
        block.label(endCatch);
        scope.generateEnsure(this);
        block.go_to(scope.getExit());
    }

    @Override
    public void generateExceptional(Exceptional node) {
        enterEmbrace(node);
        generate(node.getBegin());
        for (AstNode embrace : node.getEmbraces()) {
            generate(embrace);
        }
        leaveEmbrace();
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
        block.ldc(true);
        block.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
        block.if_acmpne(skipLabel);
        generate(node.getExpression());
        exitGuard();
        block.label(skipLabel);
    }

    @Override
    public void generateGuardCases(GuardCases node) {
        enterGuard();
        for (AstNode guard : node.getCases()) {
            generate(guard);
        }
        leaveGuard();
    }

    @Override
    public void generateHurl(Hurl node) {
        CodeBlock block = block();
        block.invokestatic(p(Errorize.class), "instance", sig(Errorize.class));
        generate(node.getBody());
        block.invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP_APPLY);
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
    public void generateLoop(Loop node) {
        LoopScope loop = enterLoop();
        generate(node.getCondition());
        loop.testCondition();
        generate(node.getBody());
        leaveLoop();
    }

    @Override
    public void generateNop(Nop nop) {
        block().aconst_null();
    }

    @Override
    public void generateReference(Reference node) {
        currentReference = node; // TODO hack
        generate(node.getLocator());
        currentReference = null;
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
        Iterator<AstNode> elements = node.getElements().iterator();
        CodeBlock block = block();
        generate(elements.next());
        while (elements.hasNext()) {
            block.pop();
            generate(elements.next());
        }
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
    public void generateTupleInitializer(TupleInitializer node) {
        try {
            CodeBlock block = block();
            List<AstNode> elements = node.getElements();
            String tupleClass = p(Class.forName("snacks.lang.Tuple" + elements.size()));
            block.newobj(tupleClass);
            block.dup();
            for (AstNode element : elements) {
                generate(element);
            }
            block.invokespecial(tupleClass, "<init>", sig(void.class, params(Object.class, elements.size())));
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void generateVariableDeclaration(VariableDeclaration node) {
        getVariable(node.getName());
        block().aconst_null();
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
        CodeBlock block = beginBlock();
        generate(node.getBody());
        if (!block.returns()) {
            block.areturn();
        }
        jiteClass().defineMethod("invoke", ACC_PUBLIC, sig(Object.class), acceptBlock());
    }

    @Override
    public void reduceReference(Reference node) {
        node.getLocator().reduce(this);
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
        acceptedClasses.add(builders.pop().getJiteClass());
    }

    private CodeBlock beginBlock() {
        return state().beginBlock();
    }

    private JiteClass beginClass(String name, List<String> interfaces) {
        JiteClass jiteClass = new JiteClass(name, p(Object.class), interfaces.toArray(new String[interfaces.size()]));
        builders.push(new ClassBuilder(jiteClass));
        return jiteClass;
    }

    private CodeBlock block() {
        return state().block();
    }

    private void enterGuard() {
        state().enterGuard();
    }

    private EmbraceScope currentEmbrace() {
        return state().currentEmbrace();
    }

    private void exitGuard() {
        state().exitGuard();
    }

    private LoopScope currentLoop() {
        return state().currentLoop();
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
        String className = "L" + jiteClass().getClassName() + ";";
        CodeBlock block = beginBlock();
        LabelNode returnValue = new LabelNode(new Label());
        jiteClass.defineField("instance", ACC_PRIVATE | ACC_STATIC, className, null);
        block.getstatic(jiteClass.getClassName(), "instance", className);
        block.ifnonnull(returnValue);
        block.newobj(jiteClass.getClassName());
        block.dup();
        block.invokespecial(jiteClass.getClassName(), "<init>", sig(void.class));
        block.putstatic(jiteClass.getClassName(), "instance", className);
        block.label(returnValue);
        block.getstatic(jiteClass.getClassName(), "instance", className);
        block.areturn();
        jiteClass.defineMethod("instance", ACC_PUBLIC | ACC_STATIC, "()" + className, acceptBlock());
        jiteClass.defineDefaultConstructor();
    }

    private void enterEmbrace(Exceptional node) {
        state().enterEmbrace(getVariable("$snacks$~exception"), node);
    }

    private LoopScope enterLoop() {
        return state().enterLoop();
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

    private String javaClass(String module, String name) {
        Class<?> actualClass = loader.loadSnack(module + '.' + name);
        if (actualClass == null) {
            String escapedName;
            if ("?".equals(name)) {
                escapedName = "$Coalesce";
            } else {
                escapedName = name.substring(0, 1).toUpperCase() + name.substring(1);
                for (String replacement : replacements.keySet()) {
                    escapedName = escapedName.replace(replacement, replacements.get(replacement));
                }
            }
            if (escapedName.startsWith("$")) {
                escapedName = "Op" + escapedName;
            }
            return module.replace('.', '/') + '/' + escapedName;
        } else {
            return actualClass.getName().replace('.', '/');
        }
    }

    private JiteClass jiteClass() {
        return state().getJiteClass();
    }

    private void leaveEmbrace() {
        state().leaveEmbrace(this);
    }

    private void leaveLoop() {
        state().leaveLoop();
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

    private void leaveGuard() {
        state().leaveGuard();
    }

    private ClassBuilder state() {
        return builders.peek();
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
}
