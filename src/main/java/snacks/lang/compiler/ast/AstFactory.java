package snacks.lang.compiler.ast;

public final class AstFactory {

    public static AstNode apply(AstNode function, AstNode argument) {
        return new Apply(function, argument);
    }

    public static AstNode constant(int value) {
        return new IntegerConstant(value);
    }

    public static AstNode constant(String value) {
        return new StringConstant(value);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AstNode> T declaration(String module, String name, AstNode body) {
        return (T) new DeclaredExpression(module, name, body);
    }

    public static AstNode reference(String name, Type type) {
        return reference("snacks/lang", name, type);
    }

    public static Reference reference(String module, String name, Type type) {
        return new Reference(module, name, type);
    }

    private AstFactory() {
        // intentionally empty
    }
}
