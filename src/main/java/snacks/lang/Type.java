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

    public static Type func(Type argument, Type result) {
        return type("->", argument, result);
    }

    public static boolean isFunction(Type type) {
        for (Type t : type.decompose()) {
            if ("->".equals(t.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInstantiable(Type type) {
        return isFunction(type) && VOID_TYPE == type.decompose().get(0).getParameters().get(0);
    }

    public static Type property(String name, Type type) {
        return type(name, type);
    }

    public static Type result(Type functionType) {
        List<Type> parameters = functionType.getParameters();
        if (parameters.isEmpty()) {
            return functionType;
        } else {
            return result(parameters.get(1));
        }
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
        List<Type> properties = new ArrayList<>();
        Iterator<Type> iterator = types.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            properties.add(property("_" + index++, iterator.next()));
        }
        return type("snacks.lang.Tuple" + types.size(), properties);
    }

    public static Type type(String name) {
        return new TypeOperator(name);
    }

    public static Type type(String name, Type... parameters) {
        return type(name, asList(parameters));
    }

    public static Type type(String name, Collection<Type> parameters) {
        return new TypeOperator(name, parameters);
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

    public abstract List<Type> getParameters();

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean occursIn(Collection<Type> types) {
        for (Type type : types) {
            if (occursIn(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean occursIn(Type type) {
        Type actualVariable = expose();
        Type actualType = type.expose();
        return actualVariable.equals(actualType) || occursIn(actualType.getParameters());
    }

    public abstract Type recompose(Type functionType, TypeFactory types);

    public abstract int size();

    public boolean unify(Type other) {
        Type left = expose();
        Type right = other.expose();
        return left.unifyLeft(right);
    }

    public abstract boolean unifyLeft(Type other);

    public boolean unifyParameters(Type other) {
        List<Type> leftParameters = getParameters();
        List<Type> rightParameters = other.getParameters();
        if (getName().equals(other.getName()) && leftParameters.size() == rightParameters.size()) {
            for (int i = 0; i < leftParameters.size(); i++) {
                if (!leftParameters.get(i).unify(rightParameters.get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public abstract boolean unifyRight(Type other);
}
