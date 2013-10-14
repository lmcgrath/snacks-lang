package snacks.lang.type;

import static java.util.Arrays.asList;

import java.util.*;

public abstract class Type {

    public boolean accepts(Type other, TypeFactory factory) {
        Type left = expose();
        Type right = other.expose();
        return left.acceptLeft(right, factory);
    }

    public void bind(Type type) {
        // intentionally empty
    }

    public List<Type> decompose() {
        return asList(this);
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract Type expose();

    public abstract void generate(TypeGenerator generator);

    public abstract Type genericCopy(TypeFactory types, Map<Type, Type> mappings);

    public abstract String getName();

    @Override
    public abstract int hashCode();

    public boolean isMember(Type type, TypeFactory factory) {
        return false;
    }

    public boolean occursIn(Type type, TypeFactory factory) {
        Type actualVariable = expose();
        Type actualType = type.expose();
        return actualVariable.equals(actualType) || actualType.contains(type, factory);
    }

    public boolean occursIn(Collection<Type> types, TypeFactory factory) {
        for (Type type : types) {
            if (occursIn(type, factory)) {
                return true;
            }
        }
        return false;
    }

    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public abstract String toString();

    protected boolean acceptLeft(Type other, TypeFactory factory) {
        return other.acceptRight(this, factory);
    }

    protected abstract boolean acceptRight(Type other, TypeFactory factory);

    protected boolean contains(Type type, TypeFactory factory) {
        return false;
    }
}
