package snacks.lang.type;

import java.util.Map;
import java.util.Objects;

public class SimpleType extends Type {

    private final String name;

    public SimpleType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof SimpleType && Objects.equals(name, ((SimpleType) o).name);
    }

    @Override
    public Type expose() {
        return this;
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateSimpleType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.copySimpleType(this, mappings);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void print(TypePrinter printer) {
        printer.printSimpleType(this);
    }

    @Override
    public String toString() {
        return "(" + name + ")";
    }
}
