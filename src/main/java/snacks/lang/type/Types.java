package snacks.lang.type;

import static java.util.Arrays.asList;

import java.util.*;
import snacks.lang.type.RecordType.Property;

public final class Types {

    public static final Type
        BOOLEAN_TYPE = simple("snacks.lang.Boolean"),
        CHARACTER_TYPE = simple("snacks.lang.Character"),
        DOUBLE_TYPE = simple("snacks.lang.Double"),
        INTEGER_TYPE = simple("snacks.lang.Integer"),
        STRING_TYPE = simple("snacks.lang.String"),
        SYMBOL_TYPE = simple("snacks.lang.Symbol"),
        VOID_TYPE = simple("snacks.lang.Void");

    private static final Unifier<?> algebraicUnifier = new AlgebraicUnifier();
    private static final Unifier<?> functionUnifier = new FunctionUnifier();
    private static final Unifier<?> recordUnifier = new RecordUnifier();
    private static final Unifier<?> recursiveUnifier = new RecursiveUnifier();
    private static final Unifier<?> simpleUnifier = new SimpleUnifier();
    private static final Unifier<?> unionUnifier = new UnionUnifier();
    private static final Unifier<?> variableUnifier = new VariableUnifier();

    public static Type algebraic(String name) {
        return algebraic(name, new ArrayList<Type>());
    }

    public static Type algebraic(String name, Collection<Type> types) {
        return algebraic(name, new ArrayList<Type>(), types);
    }

    public static Type algebraic(String name, Collection<Type> arguments, Collection<Type> options) {
        return new AlgebraicType(name, arguments, options);
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

    public static Property property(String name, Type type) {
        return new Property(name, type);
    }

    public static Type record(String name, Collection<Property> properties) {
        return record(name, new ArrayList<Type>(), properties);
    }

    public static Type record(String name, Collection<Type> arguments, Collection<Property> properties) {
        return new RecordType(name, arguments, properties);
    }

    public static Type recur(String name) {
        return recur(name, new ArrayList<Type>());
    }

    public static Type recur(String name, Collection<Type> arguments) {
        return new RecursiveType(name, arguments);
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
        return record("snacks.lang.Tuple" + types.size(), new ArrayList<Type>(), properties);
    }

    public static Unifier<?> unifierFor(Type type) {
        if (type instanceof AlgebraicType) {
            return algebraicUnifier;
        } else if (type instanceof FunctionType) {
            return functionUnifier;
        } else if (type instanceof RecordType) {
            return recordUnifier;
        } else if (type instanceof RecursiveType) {
            return recursiveUnifier;
        } else if (type instanceof SimpleType) {
            return simpleUnifier;
        } else if (type instanceof UnionType) {
            return unionUnifier;
        } else if (type instanceof VariableType) {
            return variableUnifier;
        } else {
            throw new IllegalArgumentException("No unifier for type: " + type.getClass());
        }
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

    private static boolean unifyAll(List<Type> leftTypes, List<Type> rightTypes, TypeFactory factory) {
        if (leftTypes.size() == rightTypes.size()) {
            for (int i = 0; i < leftTypes.size(); i++) {
                if (!factory.unify(leftTypes.get(i), rightTypes.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Types() {
        // intentionally empty
    }

    private static class AlgebraicUnifier extends Unifier<AlgebraicType> {

        @Override
        public boolean unifyWithAlgebraic(AlgebraicType left, AlgebraicType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyAll(left.getOptions(), right.getOptions(), factory)
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }

        @Override
        public boolean unifyWithRecursive(AlgebraicType left, RecursiveType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }

        @Override
        public boolean unifyWithRecord(AlgebraicType left, RecordType right, TypeFactory factory) {
            return contains(left, right, factory)
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }

        @Override
        public boolean unifyWithSimple(AlgebraicType left, SimpleType right, TypeFactory factory) {
            return contains(left, right, factory);
        }

        private boolean contains(AlgebraicType left, Type right, TypeFactory factory) {
            for (Type option : left.getOptions()) {
                if (factory.unify(option, right)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class FunctionUnifier extends Unifier<FunctionType> {

        @Override
        public boolean unifyWithFunction(FunctionType left, FunctionType right, TypeFactory factory) {
            return factory.unify(left.getArgument(), right.getArgument())
                && factory.unify(left.getResult(), right.getResult());
        }
    }

    private static class RecordUnifier extends Unifier<RecordType> {

        @Override
        public boolean unifyWithAlgebraic(RecordType left, AlgebraicType right, TypeFactory factory) {
            return false;
        }

        @Override
        public boolean unifyWithRecord(RecordType left, RecordType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyProperties(left.getProperties(), right.getProperties(), factory);
        }

        @Override
        public boolean unifyWithRecursive(RecordType left, RecursiveType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName());
        }

        private boolean unifyProperties(List<Property> leftProperties, List<Property> rightProperties, TypeFactory factory) {
            if (leftProperties.size() == rightProperties.size()) {
                for (int i = 0; i < leftProperties.size(); i++) {
                    Property leftProperty = leftProperties.get(i);
                    Property rightProperty = rightProperties.get(i);
                    if (!Objects.equals(leftProperty.getName(), rightProperty.getName())
                        || !factory.unify(leftProperty.getType(), rightProperty.getType())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    private static class RecursiveUnifier extends Unifier<RecursiveType> {

        @Override
        public boolean unifyWithAlgebraic(RecursiveType left, AlgebraicType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && factory.unify(factory.expand(left), right);
        }

        @Override
        public boolean unifyWithRecord(RecursiveType left, RecordType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && factory.unify(factory.expand(left), right);
        }

        @Override
        public boolean unifyWithRecursive(RecursiveType left, RecursiveType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }
    }

    private static class SimpleUnifier extends Unifier<SimpleType> {

        @Override
        public boolean unifyWithRecord(SimpleType left, RecordType right, TypeFactory factory) {
            return false;
        }

        @Override
        public boolean unifyWithSimple(SimpleType left, SimpleType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName());
        }

        @Override
        public boolean unifyWithVariable(SimpleType left, VariableType right, TypeFactory factory) {
            right.bind(left);
            return true;
        }
    }

    public static abstract class Unifier<T extends Type> {

        @SuppressWarnings("unchecked")
        public boolean unify(Type left, Type right, TypeFactory factory) {
            if (right instanceof AlgebraicType) {
                return unifyWithAlgebraic((T) left, (AlgebraicType) right, factory);
            } else if (right instanceof FunctionType) {
                return unifyWithFunction((T) left, (FunctionType) right, factory);
            } else if (right instanceof RecordType) {
                return unifyWithRecord((T) left, (RecordType) right, factory);
            } else if (right instanceof RecursiveType) {
                return unifyWithRecursive((T) left, (RecursiveType) right, factory);
            } else if (right instanceof SimpleType) {
                return unifyWithSimple((T) left, (SimpleType) right, factory);
            } else if (right instanceof UnionType) {
                return unifyWithUnion((T) left, (UnionType) right, factory);
            } else if (right instanceof VariableType) {
                return unifyWithVariable((T) left, (VariableType) right, factory);
            } else {
                throw new IllegalArgumentException("Cannot unify with " + right.getClass());
            }
        }

        public boolean unifyWithAlgebraic(T left, AlgebraicType right, TypeFactory factory) {
            throw new UnsupportedOperationException();
        }

        public boolean unifyWithFunction(T left, FunctionType right, TypeFactory factory) {
            throw new UnsupportedOperationException();
        }

        public boolean unifyWithRecord(T left, RecordType right, TypeFactory factory) {
            throw new UnsupportedOperationException();
        }

        public boolean unifyWithRecursive(T left, RecursiveType right, TypeFactory factory) {
            throw new UnsupportedOperationException();
        }

        public boolean unifyWithSimple(T left, SimpleType right, TypeFactory factory) {
            throw new UnsupportedOperationException();
        }

        public boolean unifyWithUnion(T left, UnionType right, TypeFactory factory) {
            throw new UnsupportedOperationException();
        }

        public boolean unifyWithVariable(T left, VariableType right, TypeFactory factory) {
            right.bind(left);
            return true;
        }
    }

    private static class UnionUnifier extends Unifier<UnionType> {

        @Override
        public boolean unifyWithFunction(UnionType left, FunctionType right, TypeFactory factory) {
            left.bind(right);
            return true;
        }
    }

    private static class VariableUnifier extends Unifier<VariableType> {

        @Override
        public boolean unifyWithFunction(VariableType left, FunctionType right, TypeFactory factory) {
            if (right.getArgument().equals(left) || right.getResult().equals(left)) {
                return false;
            } else {
                left.bind(right);
                return true;
            }
        }

        @Override
        public boolean unifyWithRecord(VariableType left, RecordType right, TypeFactory factory) {
            for (Property property : right.getProperties()) {
                if (property.getType().equals(left)) {
                    return false;
                }
            }
            left.bind(right);
            return true;
        }

        @Override
        public boolean unifyWithSimple(VariableType left, SimpleType right, TypeFactory factory) {
            left.bind(right);
            return true;
        }

        @Override
        public boolean unifyWithUnion(VariableType left, UnionType right, TypeFactory factory) {
            for (Type type : right.getTypes()) {
                if (left.equals(type)) {
                    return false;
                }
            }
            left.bind(right);
            return true;
        }

        @Override
        public boolean unifyWithVariable(VariableType left, VariableType right, TypeFactory factory) {
            if (!left.equals(right)) {
                left.bind(right);
            }
            return true;
        }
    }
}
