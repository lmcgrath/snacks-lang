package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class InstantiableLiteral extends Symbol implements Visitable {

    private final Symbol expression;

    public InstantiableLiteral(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitInstantiableLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof InstantiableLiteral && Objects.equals(expression, ((InstantiableLiteral) o).expression);
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
        return "(-> " + expression.toString() + ")";
    }
}
