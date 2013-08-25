package snacks.lang.compiler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.compiler.AstFactory.*;
import static snacks.lang.compiler.CompilerUtil.parse;
import static snacks.lang.compiler.TranslatorMatcher.defines;
import static snacks.lang.compiler.Type.*;

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
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldResolveTypeOfPlusWithInteger() throws SnacksException {
        translate("example = `+` 2");
        assertThat(typeOf("example"), equalTo(set(
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
        assertThat(typeOf("example"), equalTo(STRING_TYPE));
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
                environment.getReference(locator("snacks/lang", "+")),
                constant(2),
                set(
                    func(STRING_TYPE, STRING_TYPE),
                    func(DOUBLE_TYPE, DOUBLE_TYPE),
                    func(INTEGER_TYPE, INTEGER_TYPE)
                )
            ),
            reference("test", "value", STRING_TYPE),
            STRING_TYPE
        ))));
    }

    @Test(expected = TypeException.class)
    public void shouldThrowException_whenOperandTypesDontMatchOperator() throws SnacksException {
        define("oddball", type("Unknown"));
        translate(
            "import test.example.oddball",
            "example = 2 + oddball"
        );
    }

    @Test
    public void shouldSelectReferenceByNameAndType() throws SnacksException {
        define("concat", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        define("concat", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)));
        Set<AstNode> definitions = translate(
            "import test.example._",
            "example = concat 3 'waffles'"
        );
        assertThat(typeOf("example"), equalTo(STRING_TYPE));
        assertThat(definitions, defines(declaration("test", "example", apply(
            apply(
                reference("test/example", "concat", set(
                    func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)),
                    func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE))
                )),
                constant(3),
                set(
                    func(INTEGER_TYPE, INTEGER_TYPE),
                    func(STRING_TYPE, STRING_TYPE)
                )
            ),
            constant("waffles"),
            STRING_TYPE
        ))));
    }

    @Test
    public void identityFunctionShouldHaveTypeOfArgument() throws SnacksException {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "import test.example._",
            "example = identity 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToAliasImport() throws SnacksException {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "import test.example.identity as id",
            "example = id 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToImportUsingFrom() throws SnacksException {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "from test.example import identity",
            "example = identity 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToAliasImportUsingFrom() throws SnacksException {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "from test.example import identity as id",
            "example = id 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateTypedFunction() throws SnacksException {
        translate("double = (x:Integer):Integer -> x * 2");
        assertThat(typeOf("double"), equalTo(func(INTEGER_TYPE, INTEGER_TYPE)));
    }

    @Test(expected = TypeException.class)
    public void shouldNotApplyToDouble() throws SnacksException {
        translate("double = (x:Integer :: Integer -> x * 2) 2.2");
    }

    @Test
    public void shouldTranslateUntypedFunction() throws SnacksException {
        translate("double = (x) -> x * 2");
        assertThat(typeOf("double"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(STRING_TYPE, STRING_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUntypedFunctionWithMultipleArguments() throws SnacksException {
        translate("multiply = (x y) -> x * y");
        assertThat(typeOf("multiply"), equalTo(typeOf("snacks/lang", "*")));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateFunctionWithIncompatibleResultType() throws SnacksException {
        translate("multiply = (x y):String -> x * y");
    }

    @Test
    public void shouldTranslatePartiallyTypedFunction() throws SnacksException {
        translate("multiply = (x:String y) -> x * y");
        assertThat(typeOf("multiply"), equalTo(func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE))));
    }

    @Test
    public void shouldTranslateUnaryPlus() throws SnacksException {
        translate("positive = (x) -> +x");
        assertThat(typeOf("positive"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUnaryMinus() throws SnacksException {
        translate("negative = (x) -> -x");
        assertThat(typeOf("negative"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        )));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateNegativeString() throws SnacksException {
        translate("negative = (x:String) -> -x");
    }

    @Test
    public void shouldTranslateVar() throws SnacksException {
        translate("waffles = { var test = 2; return test; }()");
        assertThat(typeOf("waffles"), equalTo(INTEGER_TYPE));
    }

    private Type typeOf(String name) throws SnacksException {
        return typeOf("test", name);
    }

    private Type typeOf(String module, String name) throws SnacksException {
        return environment.getReference(locator(module, name)).getType();
    }

    private void define(String name, Type type) throws SnacksException {
        environment.define(reference("test/example", name, type));
    }

    private Set<AstNode> translate(String... inputs) throws SnacksException {
        return new Translator(environment).translate("test", parse(inputs));
    }
}
