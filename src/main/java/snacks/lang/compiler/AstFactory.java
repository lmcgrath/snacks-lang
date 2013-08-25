package snacks.lang.compiler;

import static java.util.Arrays.asList;

import java.util.List;
import snacks.lang.compiler.ast.*;

public final class AstFactory {

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

    public static AstNode func(String variable, AstNode expression, Type type) {
        return new Function(variable, expression, type);
    }

    public static AstNode instantiable(AstNode body) {
        return new Instantiable(body);
    }

    public static AstNode instantiate(AstNode invokable) {
        return new Instantiate(invokable);
    }

    public static Locator locator(String name) {
        return new VariableLocator(name);
    }

    public static Locator locator(String module, String name) {
        return new DeclarationLocator(module, name);
    }

    public static AstNode reference(String name, Type type) {
        return reference(new VariableLocator(name), type);
    }

    public static Reference reference(String module, String name, Type type) {
        return reference(new DeclarationLocator(module, name), type);
    }

    public static Reference reference(Locator locator, Type type) {
        return new Reference(locator, type);
    }

    public static AstNode sequence(AstNode... elements) {
        return sequence(asList(elements));
    }

    public static AstNode sequence(List<AstNode> elements) {
        return new Sequence(elements);
    }

    public static AstNode var(String name, AstNode value) {
        return new VariableDeclaration(name, value);
    }

    public static AstNode var(String name, Type type) {
        return new Variable(name, type);
    }

    private AstFactory() {
        // intentionally empty
    }
}
