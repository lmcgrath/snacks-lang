package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class BinaryExpression extends SyntaxNode {

    private final SyntaxNode left;
    private final SyntaxNode right;
    private final String operator;

    public BinaryExpression(String operator, SyntaxNode left, SyntaxNode right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitBinaryExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof BinaryExpression) {
            BinaryExpression other = (BinaryExpression) o;
            return Objects.equals(left, other.left)
                && Objects.equals(right, other.right)
                && Objects.equals(operator, other.operator);
        } else {
            return false;
        }
    }

    public SyntaxNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public SyntaxNode getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, operator);
    }

    @Override
    public String toString() {
        return stringify(this, operator, left, right);
    }
}
