package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.func;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Apply implements AstNode {

    private final AstNode function;
    private final AstNode argument;

    public Apply(AstNode function, AstNode argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Apply) {
            Apply other = (Apply) o;
            return new EqualsBuilder()
                .append(function, other.function)
                .append(argument, other.argument)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type getType() {
        return func(function.getType(), argument.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, argument);
    }

    @Override
    public String toString() {
        return function + "(" + argument + ")";
    }
}
