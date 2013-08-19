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
import static snacks.lang.compiler.TypeOperator.*;

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
    public void shouldResolveTypeOfPlusWithIntegers() throws SnacksException {
        translate("example = 2 + 2");
        assertThat(environment.getReference(locator("test", "example")).getType(), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldResolveTypeOfPlusWithInteger() throws SnacksException {
        translate("example = `+` 2");
        assertThat(environment.getReference(locator("test", "example")).getType(), equalTo(possibility(
            func(STRING_TYPE, STRING_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(INTEGER_TYPE, INTEGER_TYPE)
        )));
    }

    @Test
    public void shouldResolveTypeOfExpressionUsingPossibleTypes() throws SnacksException {
        translate(
            "partial = `+` 2",
            "example = partial 'bananas'"
        );
        assertThat(environment.getReference(locator("test", "example")).getType(), equalTo(STRING_TYPE));
    }

    @Test(expected = OverloadException.class)
    public void shouldNotOverloadConstantSymbolsOfSameNameAndType() throws SnacksException {
        translate(
            "oops = 2 + 3",
            "oops = 4 - 2"
        );
    }

    @Test(expected = OverloadException.class)
    public void shouldNotOverloadConstantSymbolsOfSameNameAndDifferentType() throws SnacksException {
        translate(
            "oops = 2 + 3",
            "oops = 2 + '2'"
        );
    }

    @Test
    public void shouldTranslateTwoPlusString() throws SnacksException {
        Set<AstNode> nodes = translate(
            "value = 'Hello, World!'",
            "example = 2 + value"
        );
        assertThat(nodes, defines(declaration("test", "value", constant("Hello, World!"))));
        assertThat(nodes, defines(declaration("test", "example", apply(
            apply(
                reference("+", possibility(
                    func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)),
                    func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
                    func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE))
                )),
                constant(2)
            ),
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
        Set<AstNode> definitions = translate("example = concat 3 'waffles'");
        assertThat(environment.getReference(locator("test", "example")).getType(), equalTo(STRING_TYPE));
        assertThat(definitions, defines(declaration("test", "example", apply(
            apply(
                reference("test", "concat", possibility(
                    func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)),
                    func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE))
                )),
                constant(3)
            ),
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
