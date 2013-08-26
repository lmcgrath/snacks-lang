package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class InstantiableLiteral extends Symbol implements Visitable {

    private final Symbol expression;

    public InstantiableLiteral(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitInstantiableLiteral(this);
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
