package snacks.lang.compiler;

import java.util.List;
import snacks.lang.compiler.ast.*;

public final class AstFactory {

    public static AstNode apply(AstNode function, AstNode argument) {
        Type type = function.getType();
        if (type.isParameterized()) {
            List<Type> parameters = type.getParameters();
            return apply(function, argument, parameters.get(parameters.size() - 1));
        } else {
            return apply(function, argument, type);
        }
    }

    public static AstNode apply(AstNode function, AstNode argument, Type type) {
        return new Apply(function, argument, type);
    }

    public static AstNode constant(boolean value) {
        return new BooleanConstant(value);
    }

    public static AstNode constant(double value) {
        return new DoubleConstant(value);
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

    public static Locator locator(String module, String name) {
        return new Locator(module, name);
    }

    public static AstNode reference(String name, Type type) {
        return reference("snacks/lang", name, type);
    }

    public static Reference reference(String module, String name, Type type) {
        return new Reference(new Locator(module, name), type);
    }

    private AstFactory() {
        // intentionally empty
    }
}