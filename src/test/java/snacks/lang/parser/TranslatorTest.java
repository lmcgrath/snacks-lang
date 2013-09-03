package snacks.lang.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static snacks.lang.ast.AstFactory.*;
import static snacks.lang.parser.TranslatorMatcher.defines;
import static snacks.lang.ast.Type.*;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Type;

public class TranslatorTest {

    private SymbolEnvironment environment;

    @Before
    public void setUp() {
        environment = new SymbolEnvironment();
    }

    @Test
    public void shouldResolveTypeOfPlusWithIntegers() {
        translate("example = 2 + 2");
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldResolveTypeOfPlusWithInteger() {
        translate("example = `+` 2");
        assertThat(typeOf("example"), equalTo(set(
            func(STRING_TYPE, STRING_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(INTEGER_TYPE, INTEGER_TYPE)
        )));
    }

    @Test
    public void shouldResolveTypeOfExpressionUsingPossibleTypes() {
        translate(
            "partial = `+` 2",
            "example = partial 'bananas'"
        );
        assertThat(typeOf("example"), equalTo(STRING_TYPE));
    }

    @Test
    public void shouldTranslateTwoPlusString() {
        Set<AstNode> nodes = translate(
            "value = 'Hello, World!'",
            "example = 2 + value"
        );
        assertThat(nodes, defines(declaration("test", "value", expression(constant("Hello, World!")))));
        assertThat(nodes, defines(declaration("test", "example", expression(apply(
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
        )))));
    }

    @Test(expected = TypeException.class)
    public void shouldThrowException_whenOperandTypesDontMatchOperator() {
        define("oddball", type("Unknown"));
        translate(
            "import test.example.oddball",
            "example = 2 + oddball"
        );
    }

    @Test
    public void identityFunctionShouldHaveTypeOfArgument() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
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
            "from test.example import identity as id",
            "example = id 12"
        );
        assertThat(typeOf("example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateTypedFunction() {
        translate("double = (x:Integer):Integer -> x * 2");
        assertThat(typeOf("double"), equalTo(func(INTEGER_TYPE, INTEGER_TYPE)));
    }

    @Test(expected = TypeException.class)
    public void shouldNotApplyToDouble() {
        translate("double = (x:Integer :: Integer -> x * 2) 2.2");
    }

    @Test
    public void shouldTranslateUntypedFunction() {
        translate("double = (x) -> x * 2");
        assertThat(typeOf("double"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(STRING_TYPE, STRING_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUntypedFunctionWithMultipleArguments() {
        translate("multiply = (x y) -> x * y");
        assertThat(typeOf("multiply"), equalTo(typeOf("snacks/lang", "*")));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateFunctionWithIncompatibleResultType() {
        translate("multiply = (x y):String -> x * y");
    }

    @Test
    public void shouldTranslatePartiallyTypedFunction() {
        translate("multiply = (x:String y) -> x * y");
        assertThat(typeOf("multiply"), equalTo(func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE))));
    }

    @Test
    public void shouldTranslateUnaryPlus() {
        translate("positive = (x) -> +x");
        assertThat(typeOf("positive"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUnaryMinus() {
        translate("negative = (x) -> -x");
        assertThat(typeOf("negative"), equalTo(set(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        )));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateNegativeString() {
        translate("negative = (x:String) -> -x");
    }

    @Test
    public void shouldTranslateVar() {
        translate("waffles = { var test = 2; return test; }()");
        assertThat(typeOf("waffles"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateInstantiable() {
        translate("answer = () -> 42");
        assertThat(typeOf("answer"), equalTo(func(VOID_TYPE, INTEGER_TYPE)));
    }

    @Test
    public void variableShouldOverrideSymbolInParentScope() {
        translate(
            "waffles = 24",
            "example = {",
            "    var waffles = 'I\\'m in ur scope'",
            "    return waffles",
            "}"
        );
        assertThat(typeOf("example"), equalTo(func(VOID_TYPE, STRING_TYPE)));
    }

    @Test
    public void mainShouldBeVoidToVoid() {
        translate(
            "speak = () -> say 'Woof'",
            "main = () -> speak()"
        );
        assertThat(typeOf("main"), equalTo(func(VOID_TYPE, VOID_TYPE)));
    }

    @Test
    public void shouldTranslateUntypedThreeArgFunction() {
        translate("volume = (x y z) -> x * y * z");
        assertThat(typeOf("volume").decompose(), hasSize(9));
    }

    @Test
    public void shouldTranslateVariableDeclarationAndAssignment() {
        translate(
            "test = {",
            "    var x",
            "    say 'x is declared but undefined'",
            "    x = 123",
            "    return x",
            "}"
        );
        assertThat(typeOf("test"), equalTo(func(VOID_TYPE, INTEGER_TYPE)));
    }

    @Test(expected = TypeException.class)
    public void shouldNotAssignDeclaredVariableToDifferentType() {
        translate(
            "example = {",
            "    var y",
            "    say 'y will be assigned the string \\'waffles\\', but should not be assignable to 123'",
            "    y = 'waffles'",
            "    y = 123",
            "    return y",
            "}"
        );
    }

    @Test(expected = TypeException.class)
    public void shouldNotAddBooleanToBoolean() {
        translate("example = True + True");
    }

    @Test(expected = TypeException.class)
    public void shouldNotAddBooleanToBooleanWithinFunction() {
        translate("example = () -> True + True");
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

    private Set<AstNode> translate(String... inputs) {
        return CompilerUtil.translate(environment, inputs);
    }
}
