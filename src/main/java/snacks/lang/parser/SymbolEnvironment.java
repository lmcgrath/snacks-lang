package snacks.lang.parser;

import static snacks.lang.Type.set;
import static snacks.lang.Type.type;
import static snacks.lang.Type.var;
import static snacks.lang.ast.AstFactory.reference;

import java.util.*;
import snacks.lang.*;
import snacks.lang.ast.*;

public class SymbolEnvironment implements TypeFactory {

    private final State state;

    public SymbolEnvironment(SnacksLoader loader) {
        state = new HeadState(loader);
    }

    private SymbolEnvironment(SymbolEnvironment parent) {
        state = new TailState(parent);
    }

    @Override
    public Type createVariable() {
        return state.createVariable();
    }

    public void define(Reference reference) {
        state.define(reference);
    }

    public SymbolEnvironment extend() {
        return new SymbolEnvironment(this);
    }

    @Override
    public Type genericCopy(TypeSet type, Map<Type, Type> mappings) {
        List<Type> types = new ArrayList<>();
        for (Type member : type.getMembers()) {
            types.add(genericCopy(member, mappings));
        }
        return set(types);
    }

    @Override
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

    @Override
    public Type genericCopy(TypeOperator type, Map<Type, Type> mappings) {
        List<Type> parameters = new ArrayList<>();
        for (Type parameter : type.getParameters()) {
            parameters.add(genericCopy(parameter, mappings));
        }
        return type(type.getName(), parameters);
    }

    public void generify(Type type) {
        state.generify(type);
    }

    public Reference getReference(Locator locator) {
        return state.getReference(locator);
    }

    public Collection<String> getVariables() {
        return state.getVariables();
    }

    public boolean hasSignature(Locator locator) {
        return state.hasSignature(locator);
    }

    public boolean isDefined(Locator locator) {
        return state.isDefined(locator);
    }

    public void signature(Reference reference) {
        state.signature(reference);
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

    private static abstract class State {

        protected final Map<Locator, Type> symbols;
        protected final Map<Locator, Type> signatures;
        protected final Set<Type> specializedTypes;

        public State() {
            symbols = new HashMap<>();
            signatures = new HashMap<>();
            specializedTypes = new HashSet<>();
        }

        public abstract Type createVariable();

        public void define(Reference reference) {
            if (signatures.containsKey(reference.getLocator())) {
                signatures.remove(reference.getLocator());
            }
            symbols.put(reference.getLocator(), reference.getType());
        }

        public void generify(Type type) {
            specializedTypes.remove(type);
        }

        public Reference getReference(Locator locator) {
            return reference(locator, typeOf(locator));
        }

        public Set<Type> getSpecializedTypes() {
            return new HashSet<>(specializedTypes);
        }

        public Collection<String> getVariables() {
            Set<String> variables = new TreeSet<>();
            for (Locator locator : symbols.keySet()) {
                if (locator.isVariable()) {
                    variables.add(locator.getName());
                }
            }
            return variables;
        }

        public boolean hasSignature(Locator locator) {
            return signatures.containsKey(locator);
        }

        public boolean isDefined(Locator locator) {
            return symbols.containsKey(locator) || signatures.containsKey(locator);
        }

        public void signature(Reference reference) {
            signatures.put(reference.getLocator(), reference.getType());
        }

        public void specialize(Type type) {
            specializedTypes.add(type);
        }

        public Type typeOf(Locator locator) {
            if (isDefined(locator)) {
                if (signatures.containsKey(locator)) {
                    return signatures.get(locator);
                } else {
                    return symbols.get(locator);
                }
            } else {
                throw new UndefinedSymbolException("Undefined symbol: " + locator);
            }
        }
    }

    private static final class HeadState extends State implements LocatorVisitor {

        private final SnacksLoader resolver;
        private int nextId = 1;

        public HeadState(SnacksLoader resolver) {
            this.resolver = resolver;
        }

        @Override
        public Type createVariable() {
            return var("#" + nextId++);
        }

        @Override
        public boolean isDefined(Locator locator) {
            resolve(locator);
            return super.isDefined(locator);
        }

        private void resolve(Locator locator) {
            if (!symbols.containsKey(locator)) {
                locator.accept(this);
            }
        }

        @Override
        public void visitClosureLocator(ClosureLocator locator) {
            // intentionally empty
        }

        @Override
        public void visitDeclarationLocator(DeclarationLocator locator) {
            Type type = resolver.typeOf(locator.getModule() + "." + locator.getName());
            if (type != null) {
                symbols.put(locator, type);
            }
        }

        @Override
        public void visitVariableLocator(VariableLocator locator) {
            // intentionally empty
        }
    }

    private static final class TailState extends State {

        private final SymbolEnvironment parent;

        public TailState(SymbolEnvironment parent) {
            this.parent = parent;
        }

        @Override
        public Type createVariable() {
            return parent.createVariable();
        }

        @Override
        public void generify(Type type) {
            super.generify(type);
            parent.generify(type);
        }

        @Override
        public Reference getReference(Locator locator) {
            return reference(locator, typeOf(locator));
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            Set<Type> specifics = new HashSet<>();
            specifics.addAll(super.getSpecializedTypes());
            specifics.addAll(parent.getSpecializedTypes());
            return specifics;
        }

        @Override
        public Collection<String> getVariables() {
            Set<String> variables = new TreeSet<>();
            variables.addAll(super.getVariables());
            variables.addAll(parent.getVariables());
            return variables;
        }

        @Override
        public boolean isDefined(Locator locator) {
            return super.isDefined(locator) || parent.isDefined(locator);
        }

        @Override
        public Type typeOf(Locator locator) {
            if (super.isDefined(locator)) {
                return super.typeOf(locator);
            } else {
                return parent.typeOf(locator);
            }
        }
    }
}
