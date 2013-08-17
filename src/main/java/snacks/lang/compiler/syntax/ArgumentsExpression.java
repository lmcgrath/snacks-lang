package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class ArgumentsExpression extends Symbol implements Visitable {

    private final Symbol expression;
    private final List<Symbol> arguments;

    public ArgumentsExpression(Symbol expression, Symbol... arguments) {
        this.expression = expression;
        this.arguments = asList(arguments);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitArgumentsExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ArgumentsExpression) {
            ArgumentsExpression other = (ArgumentsExpression) o;
            return new EqualsBuilder()
                .append(expression, other.expression)
                .append(arguments, other.arguments)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getArguments() {
        return arguments;
    }

    public Symbol getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, arguments);
    }

    @Override
    public String toString() {
        return expression + "(" + join(arguments, ", ") + ")";
    }
}
