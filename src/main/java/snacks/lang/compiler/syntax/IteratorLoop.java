package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class IteratorLoop extends Symbol {

    private final String variable;
    private final Symbol expression;
    private final Symbol action;
    private final Symbol defaultCase;

    public IteratorLoop(String variable, Symbol expression, Symbol action, Symbol defaultCase) {
        this.variable = variable;
        this.expression = expression;
        this.action = action;
        this.defaultCase = defaultCase;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof IteratorLoop) {
            IteratorLoop other = (IteratorLoop) o;
            return new EqualsBuilder()
                .append(variable, other.variable)
                .append(expression, other.expression)
                .append(action, other.action)
                .append(defaultCase, other.defaultCase)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, expression, action, defaultCase);
    }

    @Override
    public String toString() {
        return "(for " + variable + " in " + expression + " do " + action + (defaultCase == null ? "" : " " + defaultCase) + ")";
    }
}
