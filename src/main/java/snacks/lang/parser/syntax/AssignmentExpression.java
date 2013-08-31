package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class AssignmentExpression extends Symbol implements Visitable {

    private final Symbol target;
    private final Symbol value;

    public AssignmentExpression(Symbol target, Symbol value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAssignmentExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AssignmentExpression) {
            AssignmentExpression other = (AssignmentExpression) o;
            return new EqualsBuilder()
                .append(target, other.target)
                .append(value, other.value)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getTarget() {
        return target;
    }

    public Symbol getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, value);
    }

    @Override
    public String toString() {
        return "(" + target + " = " + value + ")";
    }
}
