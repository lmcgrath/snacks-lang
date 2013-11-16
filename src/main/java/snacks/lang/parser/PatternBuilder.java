package snacks.lang.parser;

import static java.util.Collections.reverse;
import static snacks.lang.Types.*;
import static snacks.lang.ast.AstFactory.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import snacks.lang.Type;
import snacks.lang.ast.*;

class PatternBuilder {

    private final SymbolEnvironment environment;
    private final Locator locator;
    private final List<PatternCase> patterns;

    public PatternBuilder(Locator locator, SymbolEnvironment environment) {
        this.locator = locator;
        this.environment = environment;
        this.patterns = new ArrayList<>();
    }

    public void addPattern(PatternCase pattern) {
        patterns.add(pattern);
    }

    public NamedNode toPattern() {
        Type signature = environment.getSignature(locator);
        for (PatternCase pattern : patterns) {
            analyze(pattern, signature);
        }
        List<Type> argumentTypes = getArgumentTypes(signature);
        AstNode body = patterns(getReturnType(signature), patterns);
        for (int i = argumentTypes.size() - 1; i >= 0; i--) {
            body = func(func(argumentTypes.get(i), body.getType()), "#snacks#~patternArg" + i, body);
        }
        return declaration(locator.getName(), body);
    }

    private void analyze(PatternCase pattern, Type signature) {
        List<Type> actualArguments = pattern.getMatcherTypes();
        List<Type> expectedArguments = toList(signature);
        if (actualArguments.size() >= expectedArguments.size()) {
            throw new PatternException("Type mismatch: pattern '" + locator.getName() + "' accepts more"
                + " arguments than type signature allows");
        } else {
            for (int i = 0; i < actualArguments.size(); i++) {
                Type expectedArgument = expectedArguments.get(i);
                Type actualArgument = actualArguments.get(i);
                if (!environment.unify(expectedArgument, actualArgument)) {
                    throw new PatternException("Type mismatch: pattern " + locator.getName()
                        + " argument(" + i + ") type does not match expected type " + expectedArgument);
                }
            }
            Type expectedReturnType = reify(expectedArguments.subList(
                actualArguments.size(),
                expectedArguments.size()
            ));
            if (!environment.unify(expectedReturnType, pattern.getType())) {
                throw new PatternException("Type mismatch: pattern '" + locator.getName() + "' return type does not"
                    + " match expected return type " + expectedReturnType);
            }
        }
    }

    private int getArgumentsCount() {
        return patterns.get(0).getMatcherTypes().size();
    }

    private List<Type> getArgumentTypes(Type signature) {
        return toList(signature).subList(0, getArgumentsCount());
    }

    private Type getReturnType(Type signature) {
        List<Type> types = toList(signature);
        return reify(types.subList(getArgumentsCount(), types.size()));
    }

    private Type reify(List<Type> types) {
        List<Type> reversedTypes = new ArrayList<>(types);
        reverse(reversedTypes);
        Iterator<Type> iterator = reversedTypes.iterator();
        Type type = iterator.next();
        while (iterator.hasNext()) {
            type = func(iterator.next(), type);
        }
        return type;
    }

    private List<Type> toList(Type signature) {
        if (isFunction(signature)) {
            List<Type> types = new ArrayList<>();
            Type type = signature;
            while (isFunction(type)) {
                types.add(argumentOf(type));
                type = resultOf(type);
            }
            types.add(type);
            return types;
        } else {
            throw new IllegalArgumentException("Signature is not a function");
        }
    }
}
