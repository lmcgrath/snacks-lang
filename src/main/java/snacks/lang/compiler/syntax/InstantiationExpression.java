package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class InstantiationExpression extends Symbol implements Visitable {

    private final Symbol expression;

    public InstantiationExpression(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitInstantiationExpression(this);
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
