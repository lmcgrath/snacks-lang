package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class EnsureCase extends Symbol implements Visitable {

    private final Symbol expression;

    public EnsureCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitEnsureCase(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof EnsureCase && Objects.equals(expression, ((EnsureCase) o).expression);
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
        return "(ensure " + expression + ")";
    }
}
