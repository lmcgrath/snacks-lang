package snacks.lang.parser;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.ast.AstFactory.reference;

import java.util.Collection;
import org.junit.Before;
import snacks.lang.SnackKind;
import snacks.lang.Type;
import snacks.lang.Type.VariableType;
import snacks.lang.ast.*;
import snacks.lang.runtime.SnacksClassLoader;

public abstract class AbstractTranslatorTest {

    protected SymbolEnvironment environment;

    public Type createVariable() {
        return environment.createVariable();
    }

    public void define(String name, Type type) {
        environment.define(reference(new DeclarationLocator("test.example." + name, EXPRESSION), type));
    }

    public Locator dl(String name) {
        return new DeclarationLocator(name);
    }

    public Reference ref(Locator locator) {
        return environment.getReference(locator);
    }

    @Before
    public void setUp() {
        environment = new SymbolEnvironment(new SnacksClassLoader());
    }

    public Collection<NamedNode> translate(String... inputs) {
        return CompilerUtil.translate(environment, inputs);
    }

    public Type typeOf(String qualifiedName, SnackKind kind) {
        return environment.getReference(new DeclarationLocator(qualifiedName, kind)).getType();
    }

    public Type typeOf(String qualifiedName) {
        return environment.getReference(new DeclarationLocator(qualifiedName)).getType();
    }

    public Locator vl(String name) {
        return new VariableLocator(name);
    }

    public Type vtype(String name) {
        return new VariableType(name);
    }

    public Type vtype(Type type) {
        return new VariableType(type);
    }
}
