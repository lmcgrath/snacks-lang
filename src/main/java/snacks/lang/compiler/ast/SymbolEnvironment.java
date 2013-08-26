package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.*;

import java.util.*;

public class SymbolEnvironment {

    private static final Map<String, List<Reference>> builtin = new HashMap<>();

    static {
        def("Integer", INTEGER_TYPE);
        def("String", STRING_TYPE);
        def("Boolean", BOOLEAN_TYPE);
        def("Double", DOUBLE_TYPE);

        def("+", func(BOOLEAN_TYPE, func(STRING_TYPE, STRING_TYPE)));
        def("+", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        def("+", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("+", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)));
        def("+", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("+", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        def("+", func(DOUBLE_TYPE, func(STRING_TYPE, STRING_TYPE)));
        def("+", func(STRING_TYPE, func(STRING_TYPE, STRING_TYPE)));
        def("+", func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE)));
        def("+", func(STRING_TYPE, func(DOUBLE_TYPE, STRING_TYPE)));
        def("+", func(STRING_TYPE, func(BOOLEAN_TYPE, STRING_TYPE)));

        def("-", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        def("-", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("-", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("-", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));

        def("*", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        def("*", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("*", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("*", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        def("*", func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE)));

        def("/", func(INTEGER_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        def("/", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("/", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        def("/", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));

        def("%", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));

        def("unary+", func(INTEGER_TYPE, INTEGER_TYPE));
        def("unary+", func(DOUBLE_TYPE, DOUBLE_TYPE));
        def("unary-", func(INTEGER_TYPE, INTEGER_TYPE));
        def("unary-", func(DOUBLE_TYPE, DOUBLE_TYPE));
        def("unary~", func(INTEGER_TYPE, INTEGER_TYPE));

        def("say", func(STRING_TYPE, VOID_TYPE));
    }

    private static void def(String name, Type type) {
        if (!builtin.containsKey(name)) {
            builtin.put(name, new ArrayList<Reference>());
        }
        builtin.get(name).add(new Reference(new DeclarationLocator("snacks/lang", name), type));
    }

    private final State state;

    public SymbolEnvironment() {
        state = new HeadState();
        for (List<Reference> list : builtin.values()) {
            for (Reference reference : list) {
                state.define(reference);
            }
        }
    }

    private SymbolEnvironment(SymbolEnvironment parent) {
        state = new TailState(parent);
    }

    public Type createVariable() {
        return state.createVariable();
    }

    public void define(Reference reference) {
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

    public Reference getReference(Locator locator) {
        return state.getReference(locator);
    }

    public boolean isDefined(Locator locator) {
        return state.isDefined(locator);
    }

    public void specialize(Type type) {
        state.specialize(type);
    }

    public Type typeOf(Locator locator) {
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

        void define(Reference reference);

        void generify(Type type);

        Reference getReference(Locator locator);

        Set<Type> getSpecializedTypes();

        boolean isDefined(Locator locator);

        void specialize(Type type);

        Type typeOf(Locator locator);
    }

    private static final class HeadState implements State {

        private final Map<Locator, Set<Type>> symbols;
        private final Set<Type> specializedTypes;
        private int nextId = 1;

        public HeadState() {
            this.symbols = new HashMap<>();
            this.specializedTypes = new HashSet<>();
        }

        @Override
        public Type createVariable() {
            return new TypeVariable("#" + nextId++);
        }

        @Override
        public void define(Reference reference) {
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
        public Reference getReference(Locator locator) {
            return new Reference(locator, typeOf(locator));
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
        public Type typeOf(Locator locator) {
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
        public void define(Reference reference) {
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
        public Reference getReference(Locator locator) {
            if (parent.isDefined(locator)) {
                return parent.getReference(locator);
            } else {
                return new Reference(locator, set(symbols.get(locator)));
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
        public Type typeOf(Locator locator) {
            if (isDefinedLocally(locator)) {
                Set<Type> types = new HashSet<>();
                for (Type type : symbols.get(locator)) {
                    types.add(type);
                }
                return set(types);
            } else {
                return parent.typeOf(locator);
            }
        }

        private boolean isDefinedLocally(Locator locator) {
            return symbols.containsKey(locator);
        }
    }
}