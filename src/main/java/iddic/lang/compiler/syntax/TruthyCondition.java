package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class TruthyCondition extends SyntaxNode {

    private final SyntaxNode condition;
    private final SyntaxNode expression;

    public TruthyCondition(SyntaxNode condition, SyntaxNode expression) {
        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitTruthyCondition(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TruthyCondition) {
            TruthyCondition other = (TruthyCondition) o;
            return Objects.equals(condition, other.condition)
                && Objects.equals(expression, other.expression);
        } else {
            return false;
        }
    }

    public SyntaxNode getCondition() {
        return condition;
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, expression);
    }

    @Override
    public String toString() {
        return stringify(this, condition, expression);
    }
}
