package snacks.lang.compiler;

import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static snacks.lang.compiler.Type.VOID_TYPE;
import static snacks.lang.compiler.Type.result;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.util.Set;
import com.headius.invokebinder.Binder;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JDKVersion;
import me.qmx.jitescript.JiteClass;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import snacks.lang.Expression;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.*;

public class Compiler implements AstVisitor {

    private static final Handle REFERENCE_BOOSTRAP = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(Compiler.class),
        "reference",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    public static CallSite reference(Lookup lookup, String name, MethodType type) throws ReflectiveOperationException {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle send = Binder.from(type)
            .insert(0, lookup, callSite)
            .invokeStatic(lookup, Compiler.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static Expression reference(Lookup lookup, MutableCallSite callSite, String module, String name) throws Exception {
        return new Expression() {
            @Override
            public String apply(String argument) {
                System.out.println(argument);
                return argument;
            }
        };
    }

    private JiteClass jiteClass;
    private CodeBlock codeBlock;

    public Runnable compile(final Set<AstNode> declarations) throws SnacksException {
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
            throw new SnacksException(exception);
        }
    }

    @Override
    public void visitApply(Apply node) throws SnacksException {
        compile(node.getFunction());
        compile(node.getArgument());
        codeBlock.invokevirtual(p(Expression.class), "apply", sig(String.class, String.class));
    }

    @Override
    public void visitArgument(Variable node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitBooleanConstant(BooleanConstant node) throws SnacksException {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitDeclarationLocator(DeclarationLocator locator) throws SnacksException {
        codeBlock.ldc(locator.getModule());
        codeBlock.ldc(locator.getName());
    }

    @Override
    public void visitDeclaredExpression(final DeclaredExpression node) throws SnacksException {
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
    public void visitDoubleConstant(DoubleConstant node) throws SnacksException {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitFunction(Function node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitInstantiable(Instantiable node) throws SnacksException {
        compile(node.getBody());
    }

    @Override
    public void visitInstantiate(Instantiate instantiate) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitIntegerConstant(IntegerConstant node) throws SnacksException {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitReference(Reference node) throws SnacksException {
        compile(node.getLocator());
        codeBlock.invokedynamic("reference", sig(Expression.class, String.class, String.class), REFERENCE_BOOSTRAP);
    }

    @Override
    public void visitResult(Result node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitSequence(Sequence node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitStringConstant(StringConstant node) throws SnacksException {
        codeBlock.ldc(node.getValue());
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitVariableLocator(VariableLocator locator) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    private void compile(AstNode node) throws SnacksException {
        node.accept(this);
    }

    private void compile(Locator locator) throws SnacksException {
        locator.accept(this);
    }

    private boolean hasResult(Type type, Type result) {
        return isFunction(type) && result(type).equals(result);
    }

    private boolean isFunction(Type type) {
        return "->".equals(type.getName());
    }
}
