package snacks.lang.type;

import static java.util.Arrays.asList;

import java.util.*;

public abstract class Type {

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

    public boolean occursIn(Type type) {
        Type actualVariable = expose();
        Type actualType = type.expose();
        return actualVariable.equals(actualType) || actualType.contains(type);
    }

    public boolean occursIn(Collection<Type> types) {
        for (Type type : types) {
            if (occursIn(type)) {
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

    public boolean unify(Type other) {
        Type left = expose();
        Type right = other.expose();
        return left.unifyLeft(right);
    }

    protected boolean contains(Type type) {
        return false;
    }

    protected boolean unifyLeft(Type other) {
        return other.unifyRight(this);
    }

    protected abstract boolean unifyRight(Type other);
}
