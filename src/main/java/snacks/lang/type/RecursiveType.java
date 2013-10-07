package snacks.lang.type;

import java.util.Map;
import java.util.Objects;

public class RecursiveType extends Type {

    private final String name;

    public RecursiveType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof RecursiveType && Objects.equals(name, ((RecursiveType) o).name);
    }

    @Override
    public Type expose() {
        return this;
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateRecursiveType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return this;
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
    public String toString() {
        return "(Recur " + name + ")";
    }

    @Override
    protected boolean acceptRight(Type other) {
        return Objects.equals(name, other.getName());
    }
}
