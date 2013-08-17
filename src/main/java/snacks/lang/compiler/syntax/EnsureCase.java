package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class EnsureCase extends Symbol implements Visitable {

    private final Symbol expression;

    public EnsureCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitEnsureCase(this, state);
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
