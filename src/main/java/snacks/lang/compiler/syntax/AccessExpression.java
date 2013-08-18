package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class AccessExpression extends Symbol implements Visitable {

    private final Symbol expression;
    private final String property;

    public AccessExpression(Symbol expression, String property) {
        this.expression = expression;
        this.property = property;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitAccessExpression(this, state);
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

    public Symbol getExpression() {
        return expression;
    }

    public String getProperty() {
        return property;
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