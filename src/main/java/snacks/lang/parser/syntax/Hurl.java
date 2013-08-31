package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class Hurl extends Symbol implements Visitable {

    private final Symbol expression;

    public Hurl(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitHurl(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Hurl && Objects.equals(expression, ((Hurl) o).expression);
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
