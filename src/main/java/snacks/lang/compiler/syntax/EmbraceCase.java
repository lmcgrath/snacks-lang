package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class EmbraceCase extends Symbol implements Visitable {

    private final String argument;
    private final Symbol type;
    private final Symbol expression;

    public EmbraceCase(String argument, Symbol type, Symbol expression) {
        this.argument = argument;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitEmbraceCase(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof EmbraceCase) {
            EmbraceCase other = (EmbraceCase) o;
            return new EqualsBuilder()
                .append(argument, other.argument)
                .append(type, other.type)
                .append(expression, other.expression)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getArgument() {
        return argument;
    }

    public Symbol getExpression() {
        return expression;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, type, expression);
    }

    @Override
    public String toString() {
        return "(embrace " + argument + (type == null ? "" : ":" + type) + " -> " + expression + ")";
    }
}