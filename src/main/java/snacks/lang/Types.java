package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.Type.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import snacks.lang.Type.RecordType.Property;
import snacks.lang.Type.SimpleType;

public final class Types {

    private static Type
        booleanType,
        characterType,
        doubleType,
        integerType,
        stringType,
        symbolType,
        voidType;

    public static Type algebraic(String name) {
        return algebraic(name, new ArrayList<Type>());
    }

    public static Type algebraic(String name, Iterable<Type> types) {
        return algebraic(name, new ArrayList<Type>(), types);
    }

    public static Type algebraic(String name, Iterable<Type> arguments, Iterable<Type> options) {
        return new AlgebraicType(name, arguments, options);
    }

    public static Type algebraic(Symbol name, Iterable<Type> arguments, Iterable<Type> options) {
        return new AlgebraicType(name, arguments, options);
    }

    public static Type argumentOf(Type type) {
        return isFunction(type) ? ((FunctionType) type).getArgument() : null;
    }

    public static Type func(Type argument, Type result) {
        return new FunctionType(argument, result);
    }

    public static Type booleanType() {
        if (booleanType == null) {
            booleanType = simple("snacks.lang.Boolean");
        }
        return booleanType;
    }

    public static Type characterType() {
        if (characterType == null) {
            characterType = simple("snacks.lang.Character");
        }
        return characterType;
    }

    public static Type doubleType() {
        if (doubleType == null) {
            doubleType = simple("snacks.lang.Double");
        }
        return doubleType;
    }

    public static Type integerType() {
        if (integerType == null) {
            integerType = simple("snacks.lang.Integer");
        }
        return integerType;
    }

    public static Type stringType() {
        if (stringType == null) {
            stringType = simple("snacks.lang.String");
        }
        return stringType;
    }

    public static Type symbolType() {
        if (symbolType == null) {
            symbolType = simple("snacks.lang.Symbol");
        }
        return symbolType;
    }

    public static Type voidType() {
        if (voidType == null) {
            voidType = simple("snacks.lang.Void");
        }
        return voidType;
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
        return isFunction(type) && voidType() == argumentOf(type.decompose().iterator().next());
    }

    public static Property property(String name, Type type) {
        return new Property(name, type);
    }

    public static Property property(Symbol name, Type type) {
        return new Property(name, type);
    }

    public static Type record(String name, Iterable<Property> properties) {
        return record(name, new ArrayList<Type>(), properties);
    }

    public static Type record(Symbol name, Iterable<Property> properties) {
        return record(name, new ArrayList<Type>(), properties);
    }

    public static Type record(String name, Iterable<Type> arguments, Iterable<Property> properties) {
        return new RecordType(name, arguments, properties);
    }

    public static Type record(Symbol name, Iterable<Type> arguments, Iterable<Property> properties) {
        return new RecordType(name, arguments, properties);
    }

    public static Type recur(String name) {
        return recur(name, new ArrayList<Type>());
    }

    public static Type recur(String name, Iterable<Type> arguments) {
        return new RecursiveType(name, arguments);
    }

    public static Type recur(Symbol name, Iterable<Type> arguments) {
        return new RecursiveType(name, arguments);
    }

    public static Type resultOf(Type type) {
        return isFunction(type) ? ((FunctionType) type).getResult() : null;
    }

    public static Type simple(String name) {
        return new SimpleType(name);
    }

    public static Type tuple(Type... types) {
        return tuple(asList(types));
    }

    public static Type tuple(Iterable<Type> types) {
        List<Property> properties = new ArrayList<>();
        Iterator<Type> iterator = types.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            properties.add(property("_" + index++, iterator.next()));
        }
        return record("snacks.lang.Tuple" + properties.size(), new ArrayList<Type>(), properties);
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
