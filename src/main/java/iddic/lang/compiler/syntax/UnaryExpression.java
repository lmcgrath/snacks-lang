package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class UnaryExpression extends SyntaxNode {

    private final SyntaxNode expression;
    private final String operator;

    public UnaryExpression(String operator, SyntaxNode expression) {
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitUnaryExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof UnaryExpression) {
            UnaryExpression other = (UnaryExpression) o;
            return Objects.equals(expression, other.expression)
                && Objects.equals(operator, other.operator);
        } else {
            return false;
        }
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, operator);
    }

    @Override
    public String toString() {
        return stringify(this, operator, expression);
    }
}
