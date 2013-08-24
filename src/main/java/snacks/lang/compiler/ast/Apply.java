package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class Apply implements AstNode {

    private final AstNode function;
    private final AstNode argument;
    private final Type type;

    public Apply(AstNode function, AstNode argument, Type type) {
        this.function = function;
        this.argument = argument;
        this.type = type;
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
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitApply(this, state);
    }

    public AstNode getArgument() {
        return argument;
    }

    public AstNode getFunction() {
        return function;
    }

    @Override
    public Type getType() {
        return type.expose();
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
