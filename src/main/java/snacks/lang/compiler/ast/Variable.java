package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class Variable implements AstNode {

    private final String name;
    private final Type type;

    public Variable(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitArgument(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Variable) {
            Variable other = (Variable) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public Locator getLocator() {
        return new VariableLocator(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public Reference getReference() {
        throw new IllegalStateException();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean isFunction() {
        return type.isFunction();
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public String toString() {
        return "(" + name + ":" + type + ")";
    }
}
