package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class GuardCase extends AstNode {

    private final AstNode condition;
    private final AstNode expression;

    public GuardCase(AstNode condition, AstNode expression) {
        this.condition = condition;
        this.expression = expression;
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

    @Override
    public void generate(Generator generator) {
        generator.generateGuardCase(this);
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
    public void print(AstPrinter printer) {
        printer.printGuardCase(this);
    }
}
