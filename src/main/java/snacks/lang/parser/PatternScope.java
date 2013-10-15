package snacks.lang.parser;

import static snacks.lang.type.Types.argumentOf;
import static snacks.lang.type.Types.isFunction;
import static snacks.lang.type.Types.resultOf;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Locator;
import snacks.lang.ast.Reference;
import snacks.lang.ast.VariableLocator;
import snacks.lang.type.Type;

class PatternScope {

    private final SymbolEnvironment environment;
    private final List<Type> arguments;
    private final Deque<ConstructorScope> constructors;
    private int argument = 0;

    public PatternScope(Type signature, SymbolEnvironment environment) {
        this.environment = environment;
        this.arguments = new ArrayList<>();
        this.constructors = new ArrayDeque<>();
        Type type = signature;
        while (isFunction(type)) {
            arguments.add(argumentOf(type));
            type = resultOf(type);
        }
    }

    public void beginConstructor(ConstructorScope constructor) {
        constructors.push(constructor);
    }

    public AstNode currentArgument() {
        if (constructors.isEmpty()) {
            return currentArgument(arguments.get(argument));
        } else {
            return getConstructor().accessProperty();
        }
    }

    public Reference currentArgument(Type type) {
        Locator locator = new VariableLocator("#snacks#~patternArg" + argument);
        if (!environment.isDefined(locator)) {
            environment.define(new Reference(locator, type));
        }
        return environment.getReference(locator);
    }

    public ConstructorScope getConstructor() {
        return constructors.peek();
    }

    public void leaveConstructor() {
        constructors.pop();
    }

    public void nextArgument() {
        argument++;
    }
}
