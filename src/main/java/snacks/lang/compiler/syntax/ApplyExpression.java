package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class ApplyExpression extends Symbol implements Visitable {

    private final Symbol expression;
    private final Symbol argument;

    public ApplyExpression(Symbol expression, Symbol argument) {
        this.expression = expression;
        this.argument = argument;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitApplyExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ApplyExpression) {
            ApplyExpression other = (ApplyExpression) o;
            return new EqualsBuilder()
                .append(expression, other.expression)
                .append(argument, other.argument)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getArgument() {
        return argument;
    }

    public Symbol getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, argument);
    }

    @Override
    public String toString() {
        return "(" + expression + " " + argument + ")";
    }
}
