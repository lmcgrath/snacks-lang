package snacks.lang.ast;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Type {

    public static final Type
        BOOLEAN_TYPE = type("Boolean"),
        CHARACTER_TYPE = type("Character"),
        DOUBLE_TYPE = type("Double"),
        INTEGER_TYPE = type("Integer"),
        STRING_TYPE = type("String"),
        SYMBOL_TYPE = type("Symbol"),
        VOID_TYPE = type("Void")
    ;

    public static Type func(Type argument, Type result) {
        return new TypeOperator("->", argument, result);
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
        return new TypeOperator("x", types);
    }

    public static Type type(String name) {
        return new TypeOperator(name);
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

    public abstract Type genericCopy(TypeFactory types, Map<Type, Type> mappings);

    public abstract String getName();

    public abstract List<Type> getParameters();

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
