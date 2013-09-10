package snacks.lang.parser;

import static snacks.lang.Type.set;
import static snacks.lang.Type.type;
import static snacks.lang.Type.var;
import static snacks.lang.ast.AstFactory.reference;

import java.util.*;
import snacks.lang.*;
import snacks.lang.ast.*;
import snacks.lang.parser.syntax.Operator;

public class SymbolEnvironment implements TypeFactory {

    private final State state;

    public SymbolEnvironment(SnacksLoader loader) {
        this.state = new HeadState(loader);
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

    public Operator getOperator(String name) {
        return state.getOperator(name);
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

    public boolean isOperator(String name) {
        return state.isOperator(name);
    }

    public void register(int precedence, Fixity fixity, String name) {
        state.register(precedence, fixity, name);
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

        private final Map<Locator, Type> symbols;
        private final Map<Locator, Type> signatures;
        private final Set<Type> specializedTypes;

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

        public abstract Operator getOperator(String name);

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
            resolve(locator);
            return symbols.containsKey(locator) || signatures.containsKey(locator);
        }

        public abstract boolean isOperator(String name);

        public abstract void register(int precedence, Fixity fixity, String name);

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

        protected void resolve(Locator locator) {
            // intentionally empty
        }
    }

    private static final class HeadState extends State implements LocatorVisitor {

        private final SnacksLoader loader;
        private final OperatorRegistry operators;
        private int nextId = 1;

        public HeadState(SnacksLoader loader) {
            this.loader = loader;
            this.operators = new OperatorRegistry();
        }

        @Override
        public Type createVariable() {
            return var("#" + nextId++);
        }

        @Override
        public Operator getOperator(String name) {
            return operators.isOperator(name) ? operators.getOperator(name) : loader.getOperator(name);
        }

        @Override
        public boolean isOperator(String name) {
            return operators.isOperator(name) || loader.isOperator(name);
        }

        @Override
        public void register(int precedence, Fixity fixity, String name) {
            if (loader.isOperator(name)) {
                throw new UndefinedSymbolException("Cannot redefine operator precedence for `" + name + "`");
            } else {
                operators.registerInfix(precedence, fixity, name);
            }
        }

        @Override
        public void visitClosureLocator(ClosureLocator locator) {
            // intentionally empty
        }

        @Override
        public void visitDeclarationLocator(DeclarationLocator locator) {
            Type type = loader.typeOf(locator.getModule() + "." + locator.getName());
            if (type != null) {
                define(new Reference(locator, type));
            }
        }

        @Override
        public void visitVariableLocator(VariableLocator locator) {
            // intentionally empty
        }

        @Override
        protected void resolve(Locator locator) {
            locator.accept(this);
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
        public Operator getOperator(String name) {
            return parent.getOperator(name);
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
        public boolean isOperator(String name) {
            return parent.isOperator(name);
        }

        @Override
        public void register(int precedence, Fixity fixity, String name) {
            parent.register(precedence, fixity, name);
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
