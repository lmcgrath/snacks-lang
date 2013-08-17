package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class Using extends Symbol implements Visitable {

    private final String name;
    private final Symbol expression;

    public Using(String name, Symbol expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitUsing(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Using) {
            Using other = (Using) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(expression, other.expression)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getExpression() {
        return expression;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expression);
    }

    @Override
    public String toString() {
        return "(using " + (name == null ? expression : name + " = " + expression) + ")";
    }
}
