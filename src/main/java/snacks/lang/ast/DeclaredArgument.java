package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class DeclaredArgument extends AstNode {

    private final String name;
    private final Type type;

    public DeclaredArgument(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredArgument(this);
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
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "(" + name + ":" + type + ")";
    }
}
