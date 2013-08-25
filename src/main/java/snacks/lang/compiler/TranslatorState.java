package snacks.lang.compiler;

import static java.util.Arrays.asList;
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
        define(variable.getLocator(), variable.getType());
    }

    public void define(Locator locator, Type type) throws SnacksException {
        environment().define(new Reference(locator, type));
    }

    public void enterScope() {
        environments.push(environment().extend());
    }

    public void generify(Type type) {
        environment().generify(type);
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

    public void specialize(Type type) throws SnacksException {
        environment().specialize(type);
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

    public SymbolEnvironment environment() {
        return environments.peek();
    }
}
