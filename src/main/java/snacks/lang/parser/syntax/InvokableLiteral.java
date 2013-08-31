package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class InvokableLiteral extends Symbol implements Visitable {

    private final Symbol expression;

    public InvokableLiteral(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitInvokableLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof InvokableLiteral && Objects.equals(expression, ((InvokableLiteral) o).expression);
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
