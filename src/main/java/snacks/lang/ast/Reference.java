package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class Reference extends AstNode {

    private final Locator locator;
    private final Type type;

    public Reference(Locator locator, Type type) {
        this.locator = locator;
        this.type = type;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printReference(this);
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
    public void generate(Generator generator) {
        generator.generateReference(this);
    }

    @Override
    public Type getType() {
        return type.expose();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, locator);
    }

    @Override
    public void reduce(Reducer reducer) {
        reducer.reduceReference(this);
    }

    @Override
    public String toString() {
        return "(" + locator + ":" + type + ")";
    }
}
