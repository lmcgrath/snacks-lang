package snacks.lang.parser;

import snacks.lang.Type;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Locator;
import snacks.lang.ast.NamedNode;
import snacks.lang.ast.PatternCase;
import snacks.lang.ast.Reference;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PatternCollection {

    private final Map<String, PatternBuilder> builders;
    private final Map<String, Type> signatures;
    private final Deque<PatternScope> scopes;
    private String currentPattern;

    public PatternCollection() {
        builders = new HashMap<>();
        signatures = new HashMap<>();
        scopes = new ArrayDeque<>();
    }

    public void acceptPattern(PatternCase pattern) {
        builders.get(currentPattern()).addPattern(pattern);
        currentPattern = null;
    }

    public void beginPattern(String name, Locator locator, SymbolEnvironment environment) {
        currentPattern = name;
        if (!builders.containsKey(name)) {
            signatures.put(name, environment.getSignature(locator));
            builders.put(name, new PatternBuilder(locator, environment));
        }
    }

    public Reference currentArgument(Type type) {
        return scope().currentArgument(type);
    }

    public AstNode currentArgument() {
        return scope().currentArgument();
    }

    public void enterConstructor(Type type) {
        scope().beginConstructor(new ConstructorScope(scope().currentArgument(type)));
    }

    public void enterScope(SymbolEnvironment environment) {
        scopes.push(new PatternScope(signatures.get(currentPattern()), environment));
    }

    public void leaveConstructor() {
        scope().leaveConstructor();
    }

    public void leaveScope() {
        scopes.pop();
    }

    public void nextArgument() {
        scope().nextArgument();
    }

    public List<NamedNode> render() {
        List<NamedNode> patterns = new ArrayList<>();
        for (PatternBuilder builder : builders.values()) {
            patterns.addAll(builder.toPattern());
        }
        return patterns;
    }

    public PatternScope scope() {
        return scopes.peek();
    }

    public void setProperty(String name) {
        scope().getConstructor().setProperty(name);
    }

    private String currentPattern() {
        if (currentPattern == null) {
            throw new NullPointerException("No current pattern");
        }
        return currentPattern;
    }
}
