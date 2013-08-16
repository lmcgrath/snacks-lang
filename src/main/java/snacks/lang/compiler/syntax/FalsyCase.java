package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class FalsyCase extends Symbol {

    private final Symbol condition;
    private final Symbol expression;

    public FalsyCase(Symbol condition, Symbol expression) {
        this.condition = condition;
        this.expression = expression;
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

    @Override
    public int hashCode() {
        return Objects.hash(condition, expression);
    }

    @Override
    public String toString() {
        return "(unless " + condition + " then " + expression + ")";
    }
}
