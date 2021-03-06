package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Using extends VisitableSymbol {

    private final String name;
    private final Symbol expression;

    public Using(String name, Symbol expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitUsing(this);
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
