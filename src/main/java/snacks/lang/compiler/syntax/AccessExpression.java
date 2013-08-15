package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class AccessExpression extends Symbol {

    private final Symbol expression;
    private final String property;

    public AccessExpression(Symbol expression, String property) {
        this.expression = expression;
        this.property = property;
    }

    @Override
    public String toString() {
        return expression + "." + property;
    }
}
