package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class UnaryExpression extends Symbol implements Visitable {

    private final String operator;
    private final Symbol operand;

    public UnaryExpression(String operator, Symbol operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitUnaryExpression(this, state);
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

    public Symbol getOperand() {
        return operand;
    }

    public String getOperator() {
        return operator;
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
