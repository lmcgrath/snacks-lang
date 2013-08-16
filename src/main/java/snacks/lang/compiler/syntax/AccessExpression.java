package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class AccessExpression extends Symbol {

    private final Symbol expression;
    private final String property;

    public AccessExpression(Symbol expression, String property) {
        this.expression = expression;
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AccessExpression) {
            AccessExpression other = (AccessExpression) o;
            return new EqualsBuilder()
                .append(expression, other.expression)
                .append(property, other.property)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, property);
    }

    @Override
    public String toString() {
        return expression + "." + property;
    }
}
