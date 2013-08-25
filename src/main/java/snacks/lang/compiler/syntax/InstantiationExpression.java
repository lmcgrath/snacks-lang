package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class InstantiationExpression extends Symbol implements Visitable {

    private final Symbol expression;

    public InstantiationExpression(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitInstantiationExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof InstantiationExpression && Objects.equals(expression, ((InstantiationExpression) o).expression);
    }

    public Symbol getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return "(invoke " + expression.toString() + ")";
    }
}
