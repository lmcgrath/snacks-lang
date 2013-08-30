package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class GuardCase implements AstNode {

    private final AstNode condition;
    private final AstNode expression;

    public GuardCase(AstNode condition, AstNode expression) {
        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitGuardCase(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof GuardCase) {
            GuardCase other = (GuardCase) o;
            return new EqualsBuilder()
                .append(condition, other.condition)
                .append(expression, other.expression)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getCondition() {
        return condition;
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public Type getType() {
        return expression.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, expression);
    }

    @Override
    public boolean isInvokable() {
        return false;
    }
}
