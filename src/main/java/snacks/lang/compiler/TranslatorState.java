package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static snacks.lang.compiler.AstFactory.apply;
import static snacks.lang.compiler.AstFactory.locator;

import java.util.*;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.*;

public class TranslatorState {

    private final String module;
    private final Deque<SymbolEnvironment> environments;
    private final Deque<List<AstNode>> collections;
    private final Map<String, Locator> aliases;
    private final List<String> wildcardImports;

    public TranslatorState(SymbolEnvironment environment, String module) {
        this.module = module;
        this.environments = new ArrayDeque<>(asList(environment));
        this.collections = new ArrayDeque<>();
        this.aliases = new HashMap<>();
        this.wildcardImports = new ArrayList<>();
        this.wildcardImports.add("snacks/lang");
    }

    public List<AstNode> acceptCollection() {
        return collections.pop();
    }

    public void addAlias(String alias, Locator locator) {
        aliases.put(alias, locator);
    }

    public void addWildcardImport(String wildcardImport) {
        wildcardImports.add(wildcardImport);
    }

    public void beginCollection() {
        collections.push(new ArrayList<AstNode>());
    }

    public void collect(AstNode node) {
        collections.peek().add(node);
    }

    public Type createVariable() {
        return environment().createVariable();
    }

    public void define(Variable variable) throws SnacksException {
        environment().define(new Reference(variable.getLocator(), variable.getType()));
    }

    public void enterScope() {
        environments.push(environment().extend());
    }

    public String getModule() {
        return module;
    }

    public void leaveScope() {
        environments.pop();
    }

    public Reference reference(String value) throws SnacksException {
        Locator locator;
        if (aliases.containsKey(value)) {
            locator = aliases.get(value);
        } else {
            locator = new VariableLocator(value);
            if (!environment().isDefined(locator)) {
                locator = findWildcard(value);
            }
        }
        return new Reference(locator, environment().typeOf(locator));
    }

    private Locator findWildcard(String value) throws UndefinedSymbolException {
        for (String module : wildcardImports) {
            if (environment().isDefined(locator(module, value))) {
                return locator(module, value);
            }
        }
        throw new UndefinedSymbolException("Symbol '" + value + "' is undefined");
    }

    public void register(String name, Type type) throws SnacksException {
        Locator locator = locator(module, name);
        addAlias(name, locator);
        environment().define(new Reference(locator, type));
    }

    public AstNode resolve(AstNode function, List<AstNode> arguments) throws SnacksException {
        if (!function.isFunction()) {
            throw new TypeException("Cannot apply non-function type " + function.getType() + " to arguments");
        }
        arguments = enableRemoval(arguments);
        AstNode argument = arguments.remove(0);
        ApplyTypes applyTypes = resolveApplyTypes(function, argument);
        function = dereference(function, applyTypes.functionType);
        argument = dereference(argument, applyTypes.argumentType);
        Type resultType = resolveResultType(function.getType(), argument.getType());
        AstNode expression = apply(function, argument, resultType);
        if (!arguments.isEmpty()) {
            expression = resolve(expression, arguments);
        }
        return expression;
    }

    private AstNode dereference(AstNode argument, Type argumentType) {
        if (argument.isReference()) {
            Reference argRef = argument.getReference();
            return new Reference(argRef.getLocator(), argumentType);
        } else {
            return argument;
        }
    }

    private <T> List<T> enableRemoval(List<T> list) {
        if (list instanceof LinkedList) {
            return list;
        } else {
            return new LinkedList<>(list);
        }
    }

    private SymbolEnvironment environment() {
        return environments.peek();
    }

    private ApplyTypes resolveApplyTypes(AstNode function, AstNode argument) throws TypeException {
        TypeException lastException = null;
        for (Type argumentType : resolveArgumentTypes(argument)) {
            try {
                Type functionType = resolveFunctionType(function, argumentType);
                return new ApplyTypes(functionType, argumentType);
            } catch (TypeException exception) {
                lastException = exception;
            }
        }
        throw lastException;
    }

    private List<Type> resolveArgumentTypes(AstNode argument) {
        if (argument.isReference()) {
            return argument.getReference().getPossibleTypes();
        } else {
            return asList(argument.getType());
        }
    }

    private Type resolveFunctionType(AstNode function, Type argumentType) throws TypeException {
        if (function.isReference()) {
            Reference reference = function.getReference();
            List<Type> possibilities = new ArrayList<>();
            for (Type type : reference.getPossibleTypes()) {
                if (type.isApplicableTo(argumentType)) {
                    possibilities.add(type);
                }
            }
            if (possibilities.isEmpty()) {
                throw new TypeException("Cannot apply any " + reference + " to " + argumentType);
            } else {
                return TypeOperator.possibility(possibilities);
            }
        } else {
            return function.getType();
        }
    }

    private Type resolveResultType(Type functionType, Type argumentType) throws TypeException {
        List<Type> possibleResults = new ArrayList<>();
        List<Type> variables = new ArrayList<>();
        for (Type ftype : functionType.getPossibilities()) {
            for (Type atype : argumentType.getPossibilities()) {
                List<Type> parameters = ftype.getParameters();
                Type farg = parameters.get(0).expose();
                if (atype.equals(farg)) {
                    possibleResults.add(parameters.get(1));
                } else if (farg.isVariable()) {
                    variables.add(farg);
                }
            }
        }
        if (possibleResults.isEmpty()) {
            if (variables.isEmpty()) {
                throw new TypeException("Function " + functionType + " can't be applied to " + argumentType);
            } else {
                return unify(functionType, argumentType);
            }
        } else if (possibleResults.size() == 1) {
            return possibleResults.get(0);
        } else {
            return TypeOperator.possibility(possibleResults);
        }
    }

    private Type unify(Type functionType, Type argumentType) throws TypeException {
        TypeException lastException = null;
        for (Type ftype : functionType.getPossibilities()) {
            try {
                Type resultType = createVariable();
                environment().unify(TypeOperator.func(argumentType, resultType), ftype);
                return resultType.expose();
            } catch (TypeException exception) {
                lastException = exception;
            }
        }
        throw lastException;
    }

    private static final class ApplyTypes {

        final Type functionType;
        final Type argumentType;

        public ApplyTypes(Type functionType, Type argumentType) {
            this.functionType = functionType;
            this.argumentType = argumentType;
        }
    }
}
