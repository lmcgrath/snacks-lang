package snacks.lang.compiler;

import static java.lang.Character.isLetter;
import static java.lang.Character.isUpperCase;
import static snacks.lang.compiler.AstFactory.reference;
import static snacks.lang.compiler.TypeOperator.*;

import java.util.*;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.Locator;
import snacks.lang.compiler.ast.Reference;

public class SymbolEnvironment {

    private static final Map<String, List<Reference>> builtin = new HashMap<>();

    static {
        op("+", func(BOOLEAN_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        op("+", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("+", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("+", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        op("+", func(DOUBLE_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(DOUBLE_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(BOOLEAN_TYPE, STRING_TYPE)));

        op("-", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        op("-", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("-", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("-", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));

        op("*", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        op("*", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("*", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("*", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        op("*", func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE)));

        op("/", func(INTEGER_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        op("/", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("/", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("/", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));

        op("%", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
    }

    private static void detectClash(Reference reference, Map<Locator, Set<Reference>> symbols) throws SnacksException {
        if (symbols.containsKey(reference.getLocator())) {
            for (Reference existing : symbols.get(reference.getLocator())) {
                if (!existing.isFunction() && !reference.isFunction()) {
                    throw new OverloadException("Declaration " + reference + " clashes with " + existing);
                }
            }
        }
    }

    private static void op(String operator, Type type) {
        if (!builtin.containsKey(operator)) {
            builtin.put(operator, new ArrayList<Reference>());
        }
        builtin.get(operator).add(reference("snacks/lang", operator, type));
    }

    private final State state;

    public SymbolEnvironment() {
        state = new HeadState(this);
        try {
            for (List<Reference> list : builtin.values()) {
                for (Reference reference : list) {
                    state.define(reference);
                }
            }
        } catch (SnacksException exception) {
            throw new RuntimeException(exception);
        }
    }

    private SymbolEnvironment(SymbolEnvironment parent) {
        state = new TailState(parent);
    }

    public Type createVariable() {
        return state.createVariable();
    }

    public void define(Reference reference) throws SnacksException {
        state.define(reference);
    }

    public SymbolEnvironment extend() {
        return new SymbolEnvironment(this);
    }

    public void generify(Type type) {
        state.generify(type);
    }

    public Reference getReference(Locator locator) {
        return state.getReference(locator);
    }

    public boolean isDefined(Locator locator) {
        return state.isDefined(locator);
    }

    public void specialize(Type type) {
        state.specialize(type);
    }

    public Type typeOf(Locator locator) throws SnacksException {
        List<Type> types = state.typesOf(locator);
        if (types.size() == 1) {
            return types.get(0);
        } else {
            return possibility(types);
        }
    }

    public void unify(Type left, Type right) throws TypeException {
        Type a = left.expose();
        Type b = right.expose();
        if (a.isVariable()) {
            if (!a.equals(b)) {
                if (occursIn(a, b)) {
                    throw new TypeException("Recursive unification: " + a + " != " + b);
                } else {
                    a.bind(b);
                }
            }
        } else if (b.isVariable()) {
            unify(b, a);
        } else {
            unifyParameters(a, b);
        }
    }

    private Type genericCopy(Type type) {
        return genericCopy(type, new HashMap<Type, Type>());
    }

    private Type genericCopy(Type type, HashMap<Type, Type> mappings) {
        Type actualType = type.expose();
        if (actualType.isVariable()) {
            if (isGeneric(actualType)) {
                if (!mappings.containsKey(actualType)) {
                    mappings.put(actualType, createVariable());
                }
                return mappings.get(actualType);
            } else {
                return actualType;
            }
        } else {
            List<Type> parameters = new ArrayList<>();
            for (Type parameter : actualType.getParameters()) {
                parameters.add(genericCopy(parameter, mappings));
            }
            return new TypeOperator(actualType.getName(), parameters);
        }
    }

    private Set<Type> getSpecializedTypes() {
        return state.getSpecializedTypes();
    }

    private boolean isGeneric(Type type) {
        return !occursIn(type, state.getSpecializedTypes());
    }

    private boolean occursIn(Type variable, Collection<Type> types) {
        for (Type type : types) {
            if (occursIn(variable, type)) {
                return true;
            }
        }
        return false;
    }

    private List<Type> typesOf(Locator locator) {
        return state.typesOf(locator);
    }

    private void unifyParameters(Type left, Type right) throws TypeException {
        List<Type> leftParameters = left.getParameters();
        List<Type> rightParameters = right.getParameters();
        if (left.getName().equals(right.getName()) && leftParameters.size() == rightParameters.size()) {
            for (int i = 0; i < leftParameters.size(); i++) {
                unify(leftParameters.get(i), rightParameters.get(i));
            }
        } else {
            throw new TypeException("Type mismatch: " + left + " != " + right);
        }
    }

    boolean occursIn(Type variable, Type type) {
        Type actualVariable = variable.expose();
        Type actualType = type.expose();
        return actualVariable.equals(actualType) || occursIn(actualVariable, actualType.getParameters());
    }

    private interface State {

        Type createVariable();

        void define(Reference reference) throws SnacksException;

        void generify(Type type);

        Reference getReference(Locator locator);

        Set<Type> getSpecializedTypes();

        Type getType(Locator locator) throws SnacksException;

        boolean isDefined(Locator locator);

        void specialize(Type type);

        List<Type> typesOf(Locator locator);
    }

    private static final class HeadState implements State {

        private final SymbolEnvironment parent;
        private final Map<Locator, Set<Reference>> symbols;
        private final Set<Type> specializedTypes;
        private char nextName = 'a';

        public HeadState(SymbolEnvironment parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
            this.specializedTypes = new HashSet<>();
        }

        @Override
        public Type createVariable() {
            char name = nextName++;
            while (isUpperCase(name) || !isLetter(name)) {
                name = nextName++;
                if (name >= Character.MAX_VALUE) {
                    throw new IllegalStateException("Ran out of names!");
                }
            }
            return new TypeVariable(String.valueOf(name));
        }

        @Override
        public void define(Reference reference) throws SnacksException {
            if (!symbols.containsKey(reference.getLocator())) {
                symbols.put(reference.getLocator(), new HashSet<Reference>());
            }
            detectClash(reference, symbols);
            symbols.get(reference.getLocator()).add(reference);
        }

        @Override
        public void generify(Type type) {
            specializedTypes.remove(type);
        }

        @Override
        public Reference getReference(Locator locator) {
            return symbols.get(locator).iterator().next(); // TODO
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            return new HashSet<>(specializedTypes);
        }

        @Override
        public Type getType(Locator locator) throws SnacksException {
            if (isDefined(locator)) {
                return symbols.get(locator).iterator().next().getType(); // TODO
            } else {
                throw new UndefinedSymbolException("Undefined symbol: " + locator);
            }
        }

        @Override
        public boolean isDefined(Locator locator) {
            return symbols.containsKey(locator);
        }

        @Override
        public void specialize(Type type) {
            specializedTypes.add(type);
        }

        @Override
        public List<Type> typesOf(Locator locator) {
            List<Type> types = new ArrayList<>();
            if (isDefined(locator)) {
                for (Reference reference : symbols.get(locator)) {
                    types.add(parent.genericCopy(reference.getType()));
                }
            }
            return types;
        }
    }

    private static final class TailState implements State {

        private final SymbolEnvironment parent;
        private final Map<Locator, Set<Reference>> symbols;
        private final Set<Type> specialized;

        public TailState(SymbolEnvironment parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
            this.specialized = new HashSet<>();
        }

        @Override
        public Type createVariable() {
            return parent.createVariable();
        }

        @Override
        public void define(Reference reference) throws SnacksException {
            if (!symbols.containsKey(reference.getLocator())) {
                symbols.put(reference.getLocator(), new HashSet<Reference>());
            }
            detectClash(reference, symbols);
            symbols.get(reference.getLocator()).add(reference);
        }

        @Override
        public void generify(Type type) {
            specialized.remove(type);
            parent.generify(type);
        }

        @Override
        public Reference getReference(Locator locator) {
            if (parent.isDefined(locator)) {
                return parent.getReference(locator);
            } else {
                return symbols.get(locator).iterator().next(); // TODO
            }
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            Set<Type> specifics = new HashSet<>();
            specifics.addAll(this.specialized);
            specifics.addAll(parent.getSpecializedTypes());
            return specifics;
        }

        @Override
        public Type getType(Locator locator) throws SnacksException {
            if (isDefinedLocally(locator)) {
                return symbols.get(locator).iterator().next().getType(); // TODO
            } else {
                return parent.typeOf(locator);
            }
        }

        @Override
        public boolean isDefined(Locator locator) {
            return isDefinedLocally(locator) || parent.isDefined(locator);
        }

        @Override
        public void specialize(Type type) {
            specialized.add(type);
        }

        @Override
        public List<Type> typesOf(Locator locator) {
            List<Type> types = parent.typesOf(locator);
            if (isDefinedLocally(locator)) {
                for (Reference reference : symbols.get(locator)) {
                    types.add(parent.genericCopy(reference.getType().expose()));
                }
            }
            return types;
        }

        private boolean isDefinedLocally(Locator locator) {
            return symbols.containsKey(locator);
        }
    }
}
