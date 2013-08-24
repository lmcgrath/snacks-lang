package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class InvokeExpression extends Symbol implements Visitable {

    private final Symbol expression;

    public InvokeExpression(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitInvokeExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof InvokeExpression && Objects.equals(expression, ((InvokeExpression) o).expression);
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
