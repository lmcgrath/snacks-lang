package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class DeclaredArgument implements AstNode {

    private final String name;
    private final Type type;

    public DeclaredArgument(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitDeclaredArgument(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredArgument) {
            DeclaredArgument other = (DeclaredArgument) o;
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
    public Type getType() {
        return type;
    }

    @Override
    public boolean isInvokable() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "(" + name + ":" + type + ")";
    }
}
