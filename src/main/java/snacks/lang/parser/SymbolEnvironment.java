package snacks.lang.parser;

import static snacks.lang.Type.*;
import static snacks.lang.ast.AstFactory.reference;

import java.util.*;
import snacks.lang.*;
import snacks.lang.ast.*;

public class SymbolEnvironment implements TypeFactory {

    private final SnacksRegistry registry;
    private final OperatorRegistry operators;
    private final Scope scope;

    public SymbolEnvironment(SnacksRegistry registry) {
        this.registry = registry;
        this.operators = new OperatorRegistry();
        this.scope = new HeadScope(registry);
    }

    private SymbolEnvironment(SymbolEnvironment parent) {
        this.registry = parent.registry;
        this.operators = parent.operators;
        this.scope = new TailScope(parent.scope);
    }

    @Override
    public Type createVariable() {
        return scope.createVariable();
    }

    public void define(Reference reference) {
        scope.define(reference);
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
    public Type genericCopy(RecordType type, Map<Type, Type> mappings) {
        List<PropertyType> parameters = new ArrayList<>();
        for (PropertyType parameter : type.getProperties()) {
            parameters.add((PropertyType) genericCopy(parameter, mappings));
        }
        return record(type.getName(), parameters);
    }

    @Override
    public Type genericCopy(FunctionType type, Map<Type, Type> mappings) {
        return func(genericCopy(type.getArgument(), mappings), genericCopy(type.getResult(), mappings));
    }

    @Override
    public Type genericCopy(SimpleType type, Map<Type, Type> mappings) {
        return type;
    }

    @Override
    public Type genericCopy(PropertyType type, Map<Type, Type> mappings) {
        return property(type.getName(), genericCopy(type.getType(), mappings));
    }

    public void generify(Type type) {
        scope.generify(type);
    }

    public Operator getOperator(String name) {
        return operators.isOperator(name) ? operators.getOperator(name) : registry.getOperator(name);
    }

    public Reference getReference(Locator locator) {
        return scope.getReference(locator);
    }

    public Type getSignature(Locator locator) {
        return scope.getSignature(locator);
    }

    public Collection<String> getVariables() {
        return scope.getVariables();
    }

    public boolean hasSignature(Locator locator) {
        return scope.hasSignature(locator);
    }

    public boolean isDefined(Locator locator) {
        return scope.isDefined(locator);
    }

    public boolean isOperator(String name) {
        return operators.isOperator(name) || registry.isOperator(name);
    }

    public void registerInfix(int precedence, Fixity fixity, String name) {
        if (registry.isOperator(name)) {
            throw new UndefinedSymbolException("Cannot redefine operator precedence for `" + name + "`");
        } else {
            operators.registerInfix(precedence, fixity, name);
        }
    }

    public void registerPrefix(int precedence, String name) {
        if (registry.isOperator(name)) {
            throw new UndefinedSymbolException("Cannot redefine operator precedence for `" + name + "`");
        } else {
            operators.registerPrefix(precedence, name);
        }
    }

    public void signature(Reference reference) {
        scope.signature(reference);
    }

    public void specialize(Type type) {
        scope.specialize(type);
    }

    public Type typeOf(Locator locator) {
        return genericCopy(scope.typeOf(locator), new HashMap<Type, Type>());
    }

    private Type genericCopy(Type type, Map<Type, Type> mappings) {
        return type.expose().genericCopy(this, mappings);
    }

    private boolean isGeneric(Type type) {
        return !type.occursIn(scope.getSpecializedTypes());
    }

    private static abstract class Scope {

        private final Map<Locator, Type> symbols;
        private final Map<Locator, Type> signatures;
        private final Set<Type> specializedTypes;

        public Scope() {
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

        public Type getSignature(Locator locator) {
            return signatures.get(locator);
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

        protected abstract void resolve(Locator locator);
    }

    private static final class HeadScope extends Scope implements LocatorVisitor {

        private final SnacksRegistry registry;
        private int nextId = 1;

        public HeadScope(SnacksRegistry registry) {
            this.registry = registry;
        }

        @Override
        public Type createVariable() {
            return var("#" + nextId++);
        }

        @Override
        public void visitClosureLocator(ClosureLocator locator) {
            // intentionally empty
        }

        @Override
        public void visitDeclarationLocator(DeclarationLocator locator) {
            Type type = registry.typeOf(locator.getModule() + "." + locator.getName());
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

    private static final class TailScope extends Scope {

        private final Scope parent;

        public TailScope(Scope parent) {
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

        @Override
        protected void resolve(Locator locator) {
            // intentionally empty
        }
    }
}
