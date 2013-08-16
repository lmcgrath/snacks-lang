package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class BinaryExpression extends Symbol {

    private final String operator;
    private final Symbol left;
    private final Symbol right;

    public BinaryExpression(String operator, Symbol left, Symbol right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof BinaryExpression) {
            BinaryExpression other = (BinaryExpression) o;
            return new EqualsBuilder()
                .append(operator, other.operator)
                .append(left, other.left)
                .append(right, other.right)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
