package snacks.lang.compiler.ast;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class Reference implements AstNode {

    private final Locator locator;
    private final Type type;

    public Reference(Locator locator, Type type) {
        this.locator = locator;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Reference) {
            Reference other = (Reference) o;
            return new EqualsBuilder()
                .append(locator, other.locator)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public Locator getLocator() {
        return locator;
    }

    public String getModule() {
        return locator.getModule();
    }

    public String getName() {
        return locator.getName();
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitReference(this, state);
    }

    @Override
    public Reference getReference() {
        return this;
    }

    @Override
    public Type getType() {
        return type;
    }

    public List<Type> getPossibleTypes() {
        return type.getPossibilities();
    }

    @Override
    public boolean isFunction() {
        return type.isFunction();
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, locator);
    }

    @Override
    public String toString() {
        return "(" + locator + ":" + type + ")";
    }
}
