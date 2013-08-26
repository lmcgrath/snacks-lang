package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static snacks.lang.compiler.ast.Type.VOID_TYPE;
import static snacks.lang.compiler.ast.Type.result;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import snacks.lang.compiler.ast.Type;
import snacks.lang.compiler.ast.*;

public class Compiler implements AstVisitor {

    private JiteClass jiteClass;
    private CodeBlock codeBlock;

    public Runnable compile(final Set<AstNode> declarations) throws CompileException {
        jiteClass = new JiteClass("Snacks", p(Object.class), new String[] { p(Runnable.class) });
        jiteClass.defineDefaultConstructor();
        for (AstNode declaration : declarations) {
            compile(declaration);
        }
        jiteClass.defineMethod("run", ACC_PUBLIC, sig(void.class), new CodeBlock() {{
            aload(0);
            invokespecial("Snacks", "main", sig(void.class));
            voidreturn();
        }});
        byte[] bytes = jiteClass.toBytes(JDKVersion.V1_7);
        try (FileOutputStream output = new FileOutputStream("./Snacks.class")) {
            output.write(bytes);
            Class cls = new ClassLoader(getClass().getClassLoader()) {
                public Class defineClass(String name, byte[] bytes) {
                    return super.defineClass(name, bytes, 0, bytes.length);
                }
            }.defineClass("Snacks", bytes);
            return (Runnable) cls.newInstance();
        } catch (ReflectiveOperationException | IOException exception) {
            throw new CompileException(exception);
        }
    }

    @Override
    public void visitApply(Apply node) {
        compile(node.getFunction());
        compile(node.getArgument());
        codeBlock.invokevirtual(p(Expression.class), "apply", sig(Expression.class, Expression.class));
    }

    @Override
    public void visitArgument(Variable node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitBooleanConstant(BooleanConstant node) {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitDeclarationLocator(DeclarationLocator locator) {
        codeBlock.ldc(locator.getModule());
        codeBlock.ldc(locator.getName());
    }

    @Override
    public void visitDeclaredExpression(final DeclaredExpression node) {
        final Type type = node.getType();
        if (isFunction(type)) {
            jiteClass.defineMethod(node.getName(), ACC_PUBLIC, sig(void.class), new CodeBlock() {{
                codeBlock = this;
                compile(node.getBody());
                if (hasResult(type, VOID_TYPE)) {
                    voidreturn();
                }
            }});
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void visitDoubleConstant(DoubleConstant node) {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitFunction(Function node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitInstantiable(Instantiable node) {
        compile(node.getBody());
    }

    @Override
    public void visitInstantiate(Instantiate instantiate) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitIntegerConstant(IntegerConstant node) {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitReference(Reference node) {
        compile(node.getLocator());
        codeBlock.invokedynamic("reference", sig(Expression.class, String.class, String.class), SnacksRuntime.REFERENCE_BOOTSTRAP);
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
        codeBlock.newobj(p(SnacksString.class));
        codeBlock.dup();
        codeBlock.ldc(node.getValue());
        codeBlock.invokespecial(p(SnacksString.class), "<init>", sig(void.class, String.class));
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitVariableLocator(VariableLocator locator) {
        throw new UnsupportedOperationException(); // TODO
    }

    private void compile(AstNode node) {
        node.accept(this);
    }

    private void compile(Locator locator) {
        locator.accept(this);
    }

    private boolean hasResult(Type type, Type result) {
        return isFunction(type) && result(type).equals(result);
    }

    private boolean isFunction(Type type) {
        return "->".equals(type.getName());
    }
}
