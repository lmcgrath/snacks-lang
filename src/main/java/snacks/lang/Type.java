package snacks.lang;

import static java.util.Arrays.asList;

import java.util.*;
import snacks.lang.RecordType.Property;

public abstract class Type {

    public static final Type
        BOOLEAN_TYPE = type("snacks.lang.Boolean"),
        CHARACTER_TYPE = type("snacks.lang.Character"),
        DOUBLE_TYPE = type("snacks.lang.Double"),
        INTEGER_TYPE = type("snacks.lang.Integer"),
        STRING_TYPE = type("snacks.lang.String"),
        SYMBOL_TYPE = type("snacks.lang.Symbol"),
        VOID_TYPE = type("snacks.lang.Void")
    ;

    public static Type argumentOf(Type type) {
        return isFunction(type) ? ((FunctionType) type).getArgument() : null;
    }

    public static Type func(Type argument, Type result) {
        return new FunctionType(argument, result);
    }

    public static boolean isFunction(Type type) {
        for (Type t : type.decompose()) {
            if (t instanceof FunctionType) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInstantiable(Type type) {
        return isFunction(type) && VOID_TYPE == argumentOf(type.decompose().get(0));
    }

    public static Property property(String name, Type type) {
        return new Property(name, type);
    }

    public static Type record(String name, Property... properties) {
        return new RecordType(name, asList(properties));
    }

    public static Type record(String name, Collection<Property> properties) {
        return new RecordType(name, properties);
    }

    public static Type resultOf(Type type) {
        return isFunction(type) ? ((FunctionType) type).getResult() : null;
    }

    public static Type set(Type... possibilities) {
        return new TypeSet(asList(possibilities));
    }

    public static Type set(Collection<Type> possibilities) {
        if (possibilities.size() == 1) {
            return possibilities.iterator().next();
        } else {
            return new TypeSet(possibilities);
        }
    }

    public static Type tuple(Type... types) {
        return tuple(asList(types));
    }

    public static Type tuple(Collection<Type> types) {
        List<Property> properties = new ArrayList<>();
        Iterator<Type> iterator = types.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            properties.add(property("_" + index++, iterator.next()));
        }
        return record("snacks.lang.Tuple" + types.size(), properties);
    }

    public static Type type(String name) {
        return new SimpleType(name);
    }

    public static Type var(Type type) {
        Type var = new TypeVariable("~");
        var.bind(type);
        return var;
    }

    public static Type var(String name) {
        return new TypeVariable(name);
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
