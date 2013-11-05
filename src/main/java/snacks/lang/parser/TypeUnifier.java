package snacks.lang.parser;

import java.util.List;
import java.util.Objects;
import snacks.lang.type.*;
import snacks.lang.type.RecordType.Property;

abstract class TypeUnifier<T extends Type> {

    private static final TypeUnifier<?>
        algebraicUnifier = new AlgebraicUnifier(),
        functionUnifier = new FunctionUnifier(),
        recordUnifier = new RecordUnifier(),
        recursiveUnifier = new RecursiveUnifier(),
        simpleUnifier = new SimpleUnifier(),
        unionUnifier = new UnionUnifier(),
        variableUnifier = new VariableUnifier();

    public static TypeUnifier<?> unifierFor(Type type) {
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

    private TypeUnifier() {
        // intentionally empty
    }

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

    protected boolean unifyWithAlgebraic(T left, AlgebraicType right, TypeFactory factory) {
        throw new UnsupportedOperationException();
    }

    protected boolean unifyWithFunction(T left, FunctionType right, TypeFactory factory) {
        throw new UnsupportedOperationException();
    }

    protected boolean unifyWithRecord(T left, RecordType right, TypeFactory factory) {
        throw new UnsupportedOperationException();
    }

    protected boolean unifyWithRecursive(T left, RecursiveType right, TypeFactory factory) {
        throw new UnsupportedOperationException();
    }

    protected boolean unifyWithSimple(T left, SimpleType right, TypeFactory factory) {
        throw new UnsupportedOperationException();
    }

    protected boolean unifyWithUnion(T left, UnionType right, TypeFactory factory) {
        throw new UnsupportedOperationException();
    }

    protected boolean unifyWithVariable(T left, VariableType right, TypeFactory factory) {
        right.bind(left);
        return true;
    }

    private static class AlgebraicUnifier extends TypeUnifier<AlgebraicType> {

        private boolean contains(AlgebraicType left, Type right, TypeFactory factory) {
            for (Type option : left.getOptions()) {
                if (factory.unify(option, right)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean unifyWithAlgebraic(AlgebraicType left, AlgebraicType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyAll(left.getOptions(), right.getOptions(), factory)
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }

        @Override
        protected boolean unifyWithRecord(AlgebraicType left, RecordType right, TypeFactory factory) {
            return contains(left, right, factory)
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }

        @Override
        protected boolean unifyWithRecursive(AlgebraicType left, RecursiveType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }

        @Override
        protected boolean unifyWithSimple(AlgebraicType left, SimpleType right, TypeFactory factory) {
            return contains(left, right, factory);
        }
    }

    private static class FunctionUnifier extends TypeUnifier<FunctionType> {

        @Override
        protected boolean unifyWithFunction(FunctionType left, FunctionType right, TypeFactory factory) {
            return factory.unify(left.getArgument(), right.getArgument())
                && factory.unify(left.getResult(), right.getResult());
        }
    }

    private static class RecordUnifier extends TypeUnifier<RecordType> {

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

        @Override
        protected boolean unifyWithAlgebraic(RecordType left, AlgebraicType right, TypeFactory factory) {
            return false;
        }

        @Override
        protected boolean unifyWithRecord(RecordType left, RecordType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyProperties(left.getProperties(), right.getProperties(), factory);
        }

        @Override
        protected boolean unifyWithRecursive(RecordType left, RecursiveType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName());
        }
    }

    private static class RecursiveUnifier extends TypeUnifier<RecursiveType> {

        @Override
        protected boolean unifyWithAlgebraic(RecursiveType left, AlgebraicType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && factory.unify(factory.expand(left), right);
        }

        @Override
        protected boolean unifyWithRecord(RecursiveType left, RecordType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && factory.unify(factory.expand(left), right);
        }

        @Override
        protected boolean unifyWithRecursive(RecursiveType left, RecursiveType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName())
                && unifyAll(left.getArguments(), right.getArguments(), factory);
        }
    }

    private static class SimpleUnifier extends TypeUnifier<SimpleType> {

        @Override
        protected boolean unifyWithRecord(SimpleType left, RecordType right, TypeFactory factory) {
            return false;
        }

        @Override
        protected boolean unifyWithSimple(SimpleType left, SimpleType right, TypeFactory factory) {
            return Objects.equals(left.getName(), right.getName());
        }

        @Override
        protected boolean unifyWithVariable(SimpleType left, VariableType right, TypeFactory factory) {
            right.bind(left);
            return true;
        }
    }

    private static class UnionUnifier extends TypeUnifier<UnionType> {

        @Override
        protected boolean unifyWithFunction(UnionType left, FunctionType right, TypeFactory factory) {
            left.bind(right);
            return true;
        }
    }

    private static class VariableUnifier extends TypeUnifier<VariableType> {

        @Override
        protected boolean unifyWithFunction(VariableType left, FunctionType right, TypeFactory factory) {
            if (right.getArgument().equals(left) || right.getResult().equals(left)) {
                return false;
            } else {
                left.bind(right);
                return true;
            }
        }

        @Override
        protected boolean unifyWithRecord(VariableType left, RecordType right, TypeFactory factory) {
            for (Property property : right.getProperties()) {
                if (property.getType().equals(left)) {
                    return false;
                }
            }
            left.bind(right);
            return true;
        }

        @Override
        protected boolean unifyWithSimple(VariableType left, SimpleType right, TypeFactory factory) {
            left.bind(right);
            return true;
        }

        @Override
        protected boolean unifyWithUnion(VariableType left, UnionType right, TypeFactory factory) {
            for (Type type : right.getTypes()) {
                if (left.equals(type)) {
                    return false;
                }
            }
            left.bind(right);
            return true;
        }

        @Override
        protected boolean unifyWithVariable(VariableType left, VariableType right, TypeFactory factory) {
            if (!left.equals(right)) {
                left.bind(right);
            }
            return true;
        }
    }
}
