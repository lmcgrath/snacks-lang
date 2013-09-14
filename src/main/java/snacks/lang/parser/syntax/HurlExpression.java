package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class HurlExpression extends VisitableSymbol {

    private final Symbol expression;

    public HurlExpression(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitHurl(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof HurlExpression && Objects.equals(expression, ((HurlExpression) o).expression);
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
        return "(hurl " + expression + ")";
    }
}
