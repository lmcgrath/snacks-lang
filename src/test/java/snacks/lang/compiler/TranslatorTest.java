package snacks.lang.compiler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.compiler.AstFactory.*;
import static snacks.lang.compiler.CompilerUtil.translate;
import static snacks.lang.compiler.TranslatorMatcher.defines;
import static snacks.lang.compiler.ast.Type.*;
import static snacks.lang.compiler.ast.Type.func;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import snacks.lang.compiler.ast.AstNode;
import snacks.lang.compiler.ast.SymbolEnvironment;
import snacks.lang.compiler.ast.Type;

public class TranslatorTest {

    private SymbolEnvironment environment;

    @Before
    public void setUp() {
        environment = new SymbolEnvironment();
    }

    @Test
    public void shouldResolveTypeOfPlusWithIntegers() {
        translate(environment, "example = 2 + 2");
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldResolveTypeOfPlusWithInteger() {
        translate(environment, "example = `+` 2");
        assertThat(typeOf("example"), equalTo(set(
            func(STRING_TYPE, STRING_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(INTEGER_TYPE, INTEGER_TYPE)
        )));
    }

    @Test
    public void shouldResolveTypeOfExpressionUsingPossibleTypes() {
        translate(
            environment,
            "partial = `+` 2",
            "example = partial 'bananas'"
        );
        assertThat(typeOf("example"), equalTo(STRING_TYPE));
    }

    @Test
    public void shouldTranslateTwoPlusString() {
        Set<AstNode> nodes = translate(
            environment,
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
    public void shouldThrowException_whenOperandTypesDontMatchOperator() {
        define("oddball", type("Unknown"));
        translate(
            environment,
            "import test.example.oddball",
            "example = 2 + oddball"
        );
    }

    @Test
    public void shouldSelectReferenceByNameAndType() {
        define("concat", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        define("concat", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)));
        Set<AstNode> definitions = translate(
            environment,
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
    public void identityFunctionShouldHaveTypeOfArgument() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            environment,
            "import test.example._",
            "example = identity 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToAliasImport() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            environment,
            "import test.example.identity as id",
            "example = id 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToImportUsingFrom() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            environment,
            "from test.example import identity",
            "example = identity 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToAliasImportUsingFrom() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            environment,
            "from test.example import identity as id",
            "example = id 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateTypedFunction() {
        translate(environment, "double = (x:Integer):Integer -> x * 2");
        assertThat(typeOf("double"), equalTo(func(INTEGER_TYPE, INTEGER_TYPE)));
    }

    @Test(expected = TypeException.class)
    public void shouldNotApplyToDouble() {
        translate(environment, "double = (x:Integer :: Integer -> x * 2) 2.2");
    }

    @Test
    public void shouldTranslateUntypedFunction() {
        translate(environment, "double = (x) -> x * 2");
        assertThat(typeOf("double"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(STRING_TYPE, STRING_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUntypedFunctionWithMultipleArguments() {
        translate(environment, "multiply = (x y) -> x * y");
        assertThat(typeOf("multiply"), equalTo(typeOf("snacks/lang", "*")));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateFunctionWithIncompatibleResultType() {
        translate(environment, "multiply = (x y):String -> x * y");
    }

    @Test
    public void shouldTranslatePartiallyTypedFunction() {
        translate(environment, "multiply = (x:String y) -> x * y");
        assertThat(typeOf("multiply"), equalTo(func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE))));
    }

    @Test
    public void shouldTranslateUnaryPlus() {
        translate(environment, "positive = (x) -> +x");
        assertThat(typeOf("positive"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUnaryMinus() {
        translate(environment, "negative = (x) -> -x");
        assertThat(typeOf("negative"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        )));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateNegativeString() {
        translate(environment, "negative = (x:String) -> -x");
    }

    @Test
    public void shouldTranslateVar() {
        translate(environment, "waffles = { var test = 2; return test; }()");
        assertThat(typeOf("waffles"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateInstantiable() {
        translate(environment, "answer = () -> 42");
        assertThat(typeOf("answer"), equalTo(func(VOID_TYPE, INTEGER_TYPE)));
    }

    @Test
    public void variableShouldOverrideSymbolInParentScope() {
        translate(
            environment,
            "waffles = 24",
            "example = {",
            "    var waffles = 'I\\'m in ur scope'",
            "    return waffles",
            "}"
        );
        assertThat(typeOf("example"), equalTo(func(VOID_TYPE, STRING_TYPE)));
    }

    private Type typeOf(String name) {
        return typeOf("test", name);
    }

    private Type typeOf(String module, String name) {
        return environment.getReference(locator(module, name)).getType();
    }

    private void define(String name, Type type) {
        environment.define(reference("test/example", name, type));
    }
}
