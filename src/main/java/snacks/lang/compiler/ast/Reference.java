package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Reference implements AstNode {

    private final Locator locator;
    private final Type type;

    public Reference(Locator locator, Type type) {
        this.locator = locator;
        this.type = type;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitReference(this);
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

    @Override
    public Type getType() {
        return type.expose();
    }

    @Override
    public boolean isInvokable() {
        return false;
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
