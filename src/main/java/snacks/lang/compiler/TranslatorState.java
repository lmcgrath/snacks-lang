package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static snacks.lang.compiler.AstFactory.apply;
import static snacks.lang.compiler.AstFactory.locator;
import static snacks.lang.compiler.TypeOperator.func;

import java.util.*;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.AstNode;
import snacks.lang.compiler.ast.Locator;
import snacks.lang.compiler.ast.Reference;

public class TranslatorState {

    private final String module;
    private final Deque<SymbolEnvironment> environments;
    private final Deque<List<AstNode>> collections;

    public TranslatorState(SymbolEnvironment environment, String module) {
        this.module = module;
        this.environments = new ArrayDeque<>(asList(environment));
        this.collections = new ArrayDeque<>();
    }

    public List<AstNode> acceptCollection() {
        return collections.pop();
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
        if (environment().isDefined(locator(module, value))) {
            return new Reference(locator(module, value), environment());
        } else {
            return new Reference(locator("snacks/lang", value), environment());
        }
    }

    public void register(String name, Type type) throws SnacksException {
        environment().define(new Reference(new Locator(module, name), type));
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
        Type argumentType = argument.getType();
        Type resultType = createVariable();
        AstNode expression = apply(function, argument, resultType);
        List<Type> possibilities = unify(func(argumentType, resultType), applyTypes.functionType);
        if (!arguments.isEmpty()) {
            expression = resolve(expression, arguments);
        }
        return expression;
    }

    public List<Type> unify(Type left, Type right) throws TypeException {
        return environment().unify(left, right);
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
                if (type.isFunction()) {
                    Type actualArgumentType = type.getParameters().get(0);
                    if (actualArgumentType.isVariable() || actualArgumentType.equals(argumentType)) {
                        possibilities.add(type);
                    }
                }
            }
            if (possibilities.isEmpty()) {
                throw new TypeException("Cannot apply any " + reference + " to " + argumentType);
            } else {
                return new TypePossibility(possibilities);
            }
        } else {
            return function.getType();
        }
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
