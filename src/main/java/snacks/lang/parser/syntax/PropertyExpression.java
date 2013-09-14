package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class PropertyExpression extends VisitableSymbol {

    private final String name;
    private final Symbol value;

    public PropertyExpression(String name, Symbol value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitPropertyExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PropertyExpression) {
            PropertyExpression other = (PropertyExpression) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(value, other.value)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public Symbol getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "(Property " + name + " = " + value + ")";
    }
}
