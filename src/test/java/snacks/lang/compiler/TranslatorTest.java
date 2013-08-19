package snacks.lang.compiler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.compiler.AstFactory.apply;
import static snacks.lang.compiler.AstFactory.constant;
import static snacks.lang.compiler.AstFactory.declaration;
import static snacks.lang.compiler.AstFactory.locator;
import static snacks.lang.compiler.AstFactory.reference;
import static snacks.lang.compiler.CompilerUtil.parse;
import static snacks.lang.compiler.TranslatorMatcher.defines;
import static snacks.lang.compiler.TypeOperator.INTEGER_TYPE;
import static snacks.lang.compiler.TypeOperator.STRING_TYPE;
import static snacks.lang.compiler.TypeOperator.func;
import static snacks.lang.compiler.TypeOperator.type;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.AstNode;

public class TranslatorTest {

    private SymbolEnvironment environment;

    @Before
    public void setUp() {
        environment = new SymbolEnvironment();
    }

    @Test
    public void shouldTranslateDeclaration() throws SnacksException {
        Type type = func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE));
        assertThat(translate("example = 2 + 2"), defines(declaration("test", "example", apply(
            apply(reference("+", type), constant(2)), constant(2)
        ))));
    }

    @Test
    public void shouldTranslateTwoPlusString() throws SnacksException {
        Set<AstNode> nodes = translate(
            "value = 'Hello, World!'",
            "example = 2 + value"
        );
        assertThat(nodes, defines(declaration("test", "value", constant("Hello, World!"))));
        assertThat(nodes, defines(declaration("test", "example", apply(
            apply(reference("+", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE))), constant(2)),
            reference("test", "value", STRING_TYPE)
        ))));
    }

    @Test(expected = TypeException.class)
    public void shouldThrowException_whenOperandTypesDontMatchOperator() throws SnacksException {
        environment.define(reference("test", "oddball", type("Unknown")));
        translate("example = 2 + oddball");
    }

    @Test
    public void shouldSelectReferenceByNameAndType() throws SnacksException {
        define("concat", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        define("concat", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)));
        assertThat(translate("example = concat 3 'waffles'"), defines(declaration("test", "example", apply(
            apply(reference("test", "concat", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE))), constant(3)),
            constant("waffles")
        ))));
    }

    @Test
    public void identityFunctionShouldHaveTypeOfArgument() throws SnacksException {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate("example = identity 12");
        assertThat(environment.getReference(locator("test", "example")).getType(), equalTo(INTEGER_TYPE));
    }

    private void define(String name, Type type) throws SnacksException {
        environment.define(reference("test", name, type));
    }

    private Set<AstNode> translate(String... inputs) throws SnacksException {
        return new Translator(environment).translate("test", parse(inputs));
    }
}
