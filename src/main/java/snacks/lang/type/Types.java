package snacks.lang.type;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import snacks.lang.type.RecordType.Property;

public final class Types {

    public static final Type
        BOOLEAN_TYPE = simple("snacks.lang.Boolean"),
        CHARACTER_TYPE = simple("snacks.lang.Character"),
        DOUBLE_TYPE = simple("snacks.lang.Double"),
        INTEGER_TYPE = simple("snacks.lang.Integer"),
        STRING_TYPE = simple("snacks.lang.String"),
        SYMBOL_TYPE = simple("snacks.lang.Symbol"),
        VOID_TYPE = simple("snacks.lang.Void")
            ;

    public static Type algebraic(String name) {
        return new AlgebraicType(name, new ArrayList<Type>());
    }

    public static Type algebraic(String name, Collection<Type> types) {
        return new AlgebraicType(name, types);
    }

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

    public static boolean isInvokable(Type type) {
        return isFunction(type) && VOID_TYPE == argumentOf(type.decompose().get(0));
    }

    public static boolean isType(Type type) {
        return type instanceof SimpleType || type instanceof ParameterizedType;
    }

    public static Type parameterized(Type type, Collection<Type> params) {
        return new ParameterizedType(type, params);
    }

    public static Property property(String name, Type type) {
        return new Property(name, type);
    }

    public static Type record(String name, Collection<Property> properties) {
        return new RecordType(name, properties);
    }

    public static Type recur(String name) {
        return new RecursiveType(name);
    }

    public static Type resultOf(Type type) {
        return isFunction(type) ? ((FunctionType) type).getResult() : null;
    }

    public static Type simple(String name) {
        return new SimpleType(name);
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

    public static Type union(Type... possibilities) {
        return new UnionType(asList(possibilities));
    }

    public static Type union(Collection<Type> possibilities) {
        if (possibilities.size() == 1) {
            return possibilities.iterator().next();
        } else {
            return new UnionType(possibilities);
        }
    }

    public static Type var(String name) {
        return new VariableType(name);
    }

    public static Type var(Type type) {
        return new VariableType(type);
    }

    private Types() {
        // intentionally empty
    }
}
