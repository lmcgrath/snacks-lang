package snacks.lang.parser.syntax;

import beaver.Symbol;

public class AccessExpression extends Symbol implements Visitable {

    private final Symbol expression;
    private final String property;

    public AccessExpression(Symbol expression, String property) {
        this.expression = expression;
        this.property = property;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAccessExpression(this);
    }

    public Symbol getExpression() {
        return expression;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public String toString() {
        return expression + "." + property;
    }
}
