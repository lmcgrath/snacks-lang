package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class UnaryExpression extends Symbol {

    private final String operator;
    private final Symbol operand;

    public UnaryExpression(String operator, Symbol operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof UnaryExpression) {
            UnaryExpression other = (UnaryExpression) o;
            return new EqualsBuilder()
                .append(operator, other.operator)
                .append(operand, other.operand)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, operand);
    }

    @Override
    public String toString() {
        return "(" + operator + " " + operand + ")";
    }
}
