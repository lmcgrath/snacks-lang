package snacks.lang;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SimpleType extends Type {

    private final String name;

    public SimpleType(String name) {
        this.name = name;
    }

    @Override
    public void bind(Type type) {
        // intentionally empty
    }

    @Override
    public List<Type> decompose() {
        return asList((Type) this);
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
        return types.genericCopy(this, mappings);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected boolean contains(Type type) {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public String toString() {
        return "(" + name + ")";
    }

    @Override
    public boolean unifyLeft(Type other) {
        Type left = expose();
        Type right = other.expose();
        return right.unifyRight(left);
    }

    @Override
    public boolean unifyRight(Type other) {
        return other instanceof SimpleType && name.equals(other.getName());
    }
}
