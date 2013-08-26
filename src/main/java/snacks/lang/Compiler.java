package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.c;
import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static snacks.lang.SnacksRuntime.BOOTSTRAP;
import static snacks.lang.compiler.ast.Type.VOID_TYPE;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import snacks.lang.compiler.ast.*;

public class Compiler implements AstVisitor {

    private static boolean isInstantiable(Type type) {
        return "->".equals(type.getName()) && VOID_TYPE == type.getParameters().get(0);
    }

    private final List<JiteClass> acceptedClasses;
    private final Deque<JiteClass> jiteClasses;
    private final Deque<CodeBlock> blocks;
    private String reference;

    public Compiler() {
        acceptedClasses = new ArrayList<>();
        jiteClasses = new ArrayDeque<>();
        blocks = new ArrayDeque<>();
    }

    public ClassLoader compile(Set<AstNode> declarations) throws CompileException {
        defineRunnable();
        for (AstNode declaration : declarations) {
            compile(declaration);
        }
        SnacksLoader loader = new SnacksLoader(getClass().getClassLoader());
        for (JiteClass jiteClass : acceptedClasses) {
            byte[] bytes = jiteClass.toBytes(JDKVersion.V1_7);
            try (FileOutputStream output = new FileOutputStream(jiteClass.getClassName() + ".class")) {
                output.write(bytes);
                loader.defineClass(c(jiteClass.getClassName()), bytes);
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
        block().invokedynamic("apply", sig(Object.class, Object.class, Object.class), BOOTSTRAP);
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
        reference = locator.getModule() + "/" + locator.getName();
    }

    @Override
    public void visitDeclaredExpression(DeclaredExpression node) {
        List<String> interfaces = new ArrayList<>();
        if (isInstantiable(node.getType())) {
            interfaces.add(p(Invokable.class));
        }
        JiteClass jiteClass = beginClass(new JiteClass(
            node.getModule() + "/" + node.getName(),
            p(Object.class),
            interfaces.toArray(new String[interfaces.size()])
        ));
        jiteClass.defineDefaultConstructor();
        compile(node.getBody());
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
        block().ldc(node.getValue());
    }

    @Override
    public void visitReference(Reference node) {
        compile(node.getLocator());
        CodeBlock block = block();
        block.newobj(reference);
        block.dup();
        block.invokespecial(reference, "<init>", sig(void.class));
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
        block().invokedynamic("invoke", sig(Object.class, Invokable.class), BOOTSTRAP);
    }

    @Override
    public void visitVoidFunction(VoidFunction node) {
        beginBlock();
        compile(node.getBody());
        block().areturn();
        jiteClass().defineMethod("invoke", ACC_PUBLIC, sig(Object.class), acceptBlock());
    }

    private CodeBlock acceptBlock() {
        return blocks.pop();
    }

    private void acceptClass() {
        acceptedClasses.add(jiteClasses.pop());
    }

    private CodeBlock beginBlock() {
        CodeBlock block = new CodeBlock();
        blocks.push(block);
        return block;
    }

    private JiteClass beginClass(JiteClass jiteClass) {
        jiteClasses.push(jiteClass);
        return jiteClass;
    }

    private CodeBlock block() {
        return blocks.peek();
    }

    private void compile(AstNode node) {
        node.accept(this);
    }

    private void compile(Locator locator) {
        locator.accept(this);
    }

    private void defineRunnable() {
        JiteClass jiteClass = beginClass(new JiteClass("Snacks", p(Object.class), new String[] { p(Runnable.class) }));
        jiteClass.defineDefaultConstructor();
        jiteClass.defineMethod("run", ACC_PUBLIC, sig(void.class), new CodeBlock() {{
            newobj("test/main");
            dup();
            invokespecial("test/main", "<init>", sig(void.class));
            invokevirtual("test/main", "invoke", sig(Object.class));
            voidreturn();
        }});
        acceptClass();
    }

    private JiteClass jiteClass() {
        return jiteClasses.peek();
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
