package snacks.lang.type;

import static java.util.Arrays.asList;

import java.util.*;

public abstract class Type {

    public boolean accepts(Type other) {
        Type left = expose();
        Type right = other.expose();
        return left.acceptLeft(right);
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

    public boolean isMember(Type type) {
        return false;
    }

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

    protected boolean acceptLeft(Type other) {
        return other.acceptRight(this);
    }

    protected abstract boolean acceptRight(Type other);

    protected boolean contains(Type type) {
        return false;
    }
}
