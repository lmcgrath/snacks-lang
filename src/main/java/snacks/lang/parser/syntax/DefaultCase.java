package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class DefaultCase extends Symbol implements Visitable {

    private final Symbol expression;

    public DefaultCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitDefaultCase(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DefaultCase && Objects.equals(expression, ((DefaultCase) o).expression);
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
        return "(else " + expression + ")";
    }
}
