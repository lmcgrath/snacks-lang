package snacks.lang;

import static java.util.Arrays.asList;

import java.util.*;

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

    public static PropertyType property(String name, Type type) {
        return new PropertyType(name, type);
    }

    public static Type record(String name, PropertyType... properties) {
        return new RecordType(name, asList(properties));
    }

    public static Type record(String name, Collection<PropertyType> properties) {
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
        List<PropertyType> properties = new ArrayList<>();
        Iterator<Type> iterator = types.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            properties.add(new PropertyType("_" + index++, iterator.next()));
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

    public abstract void bind(Type type);

    public abstract List<Type> decompose();

    public abstract Type expose();

    public abstract void generate(TypeGenerator generator);

    public abstract Type genericCopy(TypeFactory types, Map<Type, Type> mappings);

    public abstract String getName();

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

    public abstract Type recompose(Type functionType, TypeFactory types);

    public boolean unify(Type other) {
        Type left = expose();
        Type right = other.expose();
        return left.unifyLeft(right);
    }

    public abstract boolean unifyLeft(Type other);

    public abstract boolean unifyRight(Type other);

    protected abstract boolean contains(Type type);
}
