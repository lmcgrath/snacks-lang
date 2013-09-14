package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ApplyExpression extends VisitableSymbol {

    private final Symbol expression;
    private final Symbol argument;

    public ApplyExpression(Symbol expression, Symbol argument) {
        this.expression = expression;
        this.argument = argument;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitApplyExpression(this);
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
