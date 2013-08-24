package snacks.lang.compiler;

import static snacks.lang.compiler.AstFactory.reference;
import static snacks.lang.compiler.Type.*;

import java.util.*;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.Locator;
import snacks.lang.compiler.ast.Reference;

public class SymbolEnvironment {

    private static final Map<String, List<Reference>> builtin = new HashMap<>();

    static {
        op("Integer", INTEGER_TYPE);
        op("String", STRING_TYPE);
        op("Boolean", BOOLEAN_TYPE);
        op("Double", DOUBLE_TYPE);

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

    public Type genericCopy(TypeSet type, Map<Type, Type> mappings) {
        List<Type> types = new ArrayList<>();
        for (Type t : type.getConstrainedSet()) {
            types.add(genericCopy(t, mappings));
        }
        return new TypeSet(types);
    }

    public Type genericCopy(TypeVariable type, Map<Type, Type> mappings) {
        if (isGeneric(type)) {
            if (!mappings.containsKey(type)) {
                mappings.put(type, createVariable());
            }
            return mappings.get(type);
        } else {
            return type;
        }
    }

    public Type genericCopy(TypeOperator type, Map<Type, Type> mappings) {
        List<Type> parameters = new ArrayList<>();
        for (Type parameter : type.getParameters()) {
            parameters.add(genericCopy(parameter, mappings));
        }
        return new TypeOperator(type.getName(), parameters);
    }

    public void generify(Type type) {
        state.generify(type);
    }

    public Reference getReference(Locator locator) throws SnacksException {
        return state.getReference(locator);
    }

    public boolean isDefined(Locator locator) {
        return state.isDefined(locator);
    }

    public void specialize(Type type) {
        state.specialize(type);
    }

    public Type typeOf(Locator locator) throws SnacksException {
        return genericCopy(state.typeOf(locator), new HashMap<Type, Type>());
    }

    private Type genericCopy(Type type, Map<Type, Type> mappings) {
        return type.expose().genericCopy(this, mappings);
    }

    private Set<Type> getSpecializedTypes() {
        return state.getSpecializedTypes();
    }

    private boolean isGeneric(Type type) {
        return !type.occursIn(state.getSpecializedTypes());
    }

    private interface State {

        Type createVariable();

        void define(Reference reference) throws SnacksException;

        void generify(Type type);

        Reference getReference(Locator locator) throws SnacksException;

        Set<Type> getSpecializedTypes();

        boolean isDefined(Locator locator);

        void specialize(Type type);

        Type typeOf(Locator locator) throws SnacksException;
    }

    private static final class HeadState implements State {

        private final SymbolEnvironment parent;
        private final Map<Locator, Set<Type>> symbols;
        private final Set<Type> specializedTypes;
        private int nextId = 1;

        public HeadState(SymbolEnvironment parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
            this.specializedTypes = new HashSet<>();
        }

        @Override
        public Type createVariable() {
            return new TypeVariable("#" + nextId++);
        }

        @Override
        public void define(Reference reference) throws SnacksException {
            if (!symbols.containsKey(reference.getLocator())) {
                symbols.put(reference.getLocator(), new HashSet<Type>());
            }
            symbols.get(reference.getLocator()).add(reference.getType());
        }

        @Override
        public void generify(Type type) {
            specializedTypes.remove(type);
        }

        @Override
        public Reference getReference(Locator locator) throws SnacksException {
            return reference(locator, typeOf(locator));
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            return new HashSet<>(specializedTypes);
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
        public Type typeOf(Locator locator) throws SnacksException {
            if (isDefined(locator)) {
                return set(symbols.get(locator));
            } else {
                throw new UndefinedSymbolException("Undefined symbol: " + locator);
            }
        }
    }

    private static final class TailState implements State {

        private final SymbolEnvironment parent;
        private final Map<Locator, Set<Type>> symbols;
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
                symbols.put(reference.getLocator(), new HashSet<Type>());
            }
            symbols.get(reference.getLocator()).add(reference.getType());
        }

        @Override
        public void generify(Type type) {
            specialized.remove(type);
            parent.generify(type);
        }

        @Override
        public Reference getReference(Locator locator) throws SnacksException {
            if (parent.isDefined(locator)) {
                return parent.getReference(locator);
            } else {
                return reference(locator, set(symbols.get(locator)));
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
        public boolean isDefined(Locator locator) {
            return isDefinedLocally(locator) || parent.isDefined(locator);
        }

        @Override
        public void specialize(Type type) {
            specialized.add(type);
        }

        @Override
        public Type typeOf(Locator locator) throws SnacksException {
            Set<Type> types = new HashSet<>();
            if (isDefinedLocally(locator)) {
                for (Type type : symbols.get(locator)) {
                    types.add(type);
                }
            }
            if (parent.isDefined(locator)) {
                types.addAll(parent.typeOf(locator).decompose());
            }
            return set(types);
        }

        private boolean isDefinedLocally(Locator locator) {
            return symbols.containsKey(locator);
        }
    }
}
