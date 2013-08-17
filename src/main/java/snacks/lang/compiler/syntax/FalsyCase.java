package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class FalsyCase extends Symbol implements Visitable {

    private final Symbol condition;
    private final Symbol expression;

    public FalsyCase(Symbol condition, Symbol expression) {
        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitFalsyCase(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FalsyCase) {
            FalsyCase other = (FalsyCase) o;
            return new EqualsBuilder()
                .append(condition, other.condition)
                .append(expression, other.expression)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getCondition() {
        return condition;
    }

    public Symbol getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, expression);
    }

    @Override
    public String toString() {
        return "(unless " + condition + " then " + expression + ")";
    }
}
