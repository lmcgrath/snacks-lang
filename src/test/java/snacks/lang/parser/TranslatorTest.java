package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.ast.AstFactory.*;
import static snacks.lang.ast.AstFactory.var;
import static snacks.lang.parser.TranslatorMatcher.defines;
import static snacks.lang.type.Types.*;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.record;

import java.util.Collection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import snacks.lang.SnackKind;
import snacks.lang.ast.*;
import snacks.lang.runtime.SnacksClassLoader;
import snacks.lang.type.Type;
import snacks.lang.type.VariableType;

public class TranslatorTest {

    private SymbolEnvironment environment;

    @Before
    public void setUp() {
        environment = new SymbolEnvironment(new SnacksClassLoader());
    }

    @Test
    public void shouldResolveTypeOfPlusWithIntegers() {
        translate("example = 2 + 2");
        assertThat(typeOf("test.example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldResolveTypeOfPlusWithInteger() {
        translate("example = (+) 2");
        assertThat(typeOf("test.example"), equalTo(union(
            func(STRING_TYPE, STRING_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(INTEGER_TYPE, INTEGER_TYPE)
        )));
    }

    @Test
    public void shouldResolveTypeOfExpressionUsingPossibleTypes() {
        translate(
            "partial = (+) 2",
            "example = partial 'bananas'"
        );
        assertThat(typeOf("test.example"), equalTo(STRING_TYPE));
    }

    @Test
    public void shouldTranslateTwoPlusString() {
        Collection<NamedNode> nodes = translate(
            "value = 'Hello, World!'",
            "example = 2 + value"
        );
        assertThat(nodes, defines(declaration("test.value", expression(constant("Hello, World!")))));
        assertThat(nodes, defines(declaration("test.example", expression(apply(
            apply(
                environment.getReference(new DeclarationLocator("snacks.lang.+")),
                constant(2),
                union(
                    func(STRING_TYPE, STRING_TYPE),
                    func(DOUBLE_TYPE, DOUBLE_TYPE),
                    func(INTEGER_TYPE, INTEGER_TYPE)
                )
            ),
            reference(new DeclarationLocator("test.value"), STRING_TYPE),
            STRING_TYPE
        )))));
    }

    @Test(expected = TypeException.class)
    public void shouldThrowException_whenOperandTypesDontMatchOperator() {
        define("oddball", simple("Unknown"));
        translate(
            "import test.example.oddball",
            "example = 2 + oddball"
        );
    }

    @Test
    public void identityFunctionShouldHaveTypeOfArgument() {
        translate(
            "identity = (a) -> a",
            "example = identity 12"
        );
        assertThat(typeOf("test.example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToAliasImport() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "import test.example.identity as id",
            "example = id 12"
        );
        assertThat(typeOf("test.example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToImportUsingFrom() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "from test.example import identity",
            "example = identity 12"
        );
        assertThat(typeOf("test.example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldBeAbleToAliasImportUsingFrom() {
        Type var = environment.createVariable();
        define("identity", func(var, var));
        translate(
            "from test.example import identity as id",
            "example = id 12"
        );
        assertThat(typeOf("test.example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateTypedFunction() {
        translate("double = (x:Integer):Integer -> x * 2");
        assertThat(typeOf("test.double"), equalTo(func(INTEGER_TYPE, INTEGER_TYPE)));
    }

    @Test(expected = TypeException.class)
    public void shouldNotApplyToDouble() {
        translate("double = (x:Integer -> x * 2) 2.2");
    }

    @Test
    public void shouldTranslateUntypedFunction() {
        translate("double = (x) -> x * 2");
        assertThat(typeOf("test.double"), equalTo(union(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE),
            func(STRING_TYPE, STRING_TYPE)
        )));
    }

    @Test
    public void shouldTranslateUntypedFunctionWithMultipleArguments() {
        translate("multiply = (x y) -> x * y");
        assertThat(typeOf("test.multiply"), equalTo(typeOf("snacks.lang.*")));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateFunctionWithIncompatibleResultType() {
        translate("multiply = (x y):String -> x * y");
    }

    @Test
    public void shouldTranslatePartiallyTypedFunction() {
        translate("multiply = (x:String y) -> x * y");
        assertThat(typeOf("test.multiply"), equalTo(func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE))));
    }

    @Test
    public void shouldTranslateUnaryPlus() {
        translate("positive = (x) -> +x");
        assertThat(typeOf("test.positive"), equalTo(typeOf("snacks.lang.unary+")));
    }

    @Test
    public void shouldTranslateUnaryMinus() {
        translate("negative = (x) -> -x");
        assertThat(typeOf("test.negative"), equalTo(typeOf("snacks.lang.unary-")));
    }

    @Test(expected = TypeException.class)
    public void shouldNotTranslateNegativeString() {
        translate("negative = (x:String) -> -x");
    }

    @Test
    public void shouldTranslateVar() {
        translate("waffles = { var test = 2; return test; } ()");
        assertThat(typeOf("test.waffles"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldTranslateInstantiable() {
        translate("answer = () -> 42");
        assertThat(typeOf("test.answer"), equalTo(func(VOID_TYPE, INTEGER_TYPE)));
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
        assertThat(typeOf("test.example"), equalTo(func(VOID_TYPE, STRING_TYPE)));
    }

    @Test
    public void mainShouldBeVoidToVoid() {
        translate(
            "speak = () -> say 'Woof'",
            "main = () -> speak ()"
        );
        assertThat(typeOf("test.main"), equalTo(func(VOID_TYPE, VOID_TYPE)));
    }

    @Test
    public void shouldTranslateUntypedThreeArgFunction() {
        translate("volume = (x y z) -> x * y * z");
        assertThat(typeOf("test.volume").decompose(), hasSize(9));
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
        assertThat(typeOf("test.test"), equalTo(func(VOID_TYPE, INTEGER_TYPE)));
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

    @Test
    public void shouldApplyFunctionToFunction() {
        translate(
            "operate :: (Integer -> Integer -> Integer) -> Integer",
            "operate = (op) -> op 2 4",
            "example = operate (+)"
        );
        assertThat(typeOf("test.example"), equalTo(INTEGER_TYPE));
    }

    @Test
    public void shouldSpecifySignature() {
        translate(
            "addIntegers :: Integer -> Integer -> Integer",
            "addIntegers = (x y) -> x + y"
        );
        assertThat(typeOf("test.addIntegers"), equalTo(func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE))));
    }

    @Test(expected = TypeException.class)
    public void shouldNotAccessNonexistentMemberOfTuple() {
        translate("main = () -> say ('waffles', 10, True)._3");
    }

    @Test(expected = MissingPropertyException.class)
    public void shouldRequireAllPropertiesOnRecord() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ stringy SideForBacon {",
            "    name = 'Waffles',",
            "    tasteIndex = 10,",
            "}"
        );
    }

    @Test(expected = TypeException.class)
    public void shouldRequirePropertyTypesToMatch() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ stringy SideForBacon {",
            "    name = 'Waffles',",
            "    tasteIndex = 10,",
            "    pairsWithBacon? = 'eewwwww bacon is grody'",
            "}"
        );
    }

    @Test(expected = DuplicatePropertyException.class)
    public void shouldNotAllowDuplicateProperties() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ stringy SideForBacon {",
            "    pairsWithBacon? = True,",
            "    name = 'Waffles',",
            "    tasteIndex = 10,",
            "    pairsWithBacon? = False // can't make up my mind! )=",
            "}"
        );
    }

    @Test
    public void recordShouldCreateAlgebraicType() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}"
        );
        assertThat(typeOf("test.BreakfastItem", TYPE), equalTo(algebraic("test.BreakfastItem", asList(
            record("test.SideForBacon", asList(
                property("name", STRING_TYPE),
                property("tasteIndex", INTEGER_TYPE),
                property("pairsWithBacon?", BOOLEAN_TYPE)
            ))
        ))));
    }

    @Test
    public void recordWithSameConstructorAsTypeShouldBeSingleType() {
        translate(
            "data BreakfastItem = BreakfastItem {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}"
        );
        assertThat(typeOf("test.BreakfastItem", TYPE), equalTo(record("test.BreakfastItem", asList(
            property("name", STRING_TYPE),
            property("tasteIndex", INTEGER_TYPE),
            property("pairsWithBacon?", BOOLEAN_TYPE)
        ))));
    }

    @Test
    public void recordWithoutConstructorShouldDefineConstructorWithNameOfType() {
        translate(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}"
        );
        assertThat(typeOf("test.BreakfastItem", TYPE), equalTo(record("test.BreakfastItem", asList(
            property("name", STRING_TYPE),
            property("tasteIndex", INTEGER_TYPE),
            property("pairsWithBacon?", BOOLEAN_TYPE)
        ))));
    }

    @Test
    public void recordShouldCreateInstanceType() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}"
        );
        assertThat(typeOf("test.SideForBacon", TYPE), equalTo(record("test.SideForBacon", asList(
            property("name", STRING_TYPE),
            property("tasteIndex", INTEGER_TYPE),
            property("pairsWithBacon?", BOOLEAN_TYPE)
        ))));
    }

    @Test
    public void recordShouldCreateConstructorReturningInstanceType() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}"
        );
        assertThat(typeOf("test.SideForBacon", EXPRESSION), equalTo(
            func(STRING_TYPE, func(INTEGER_TYPE, func(BOOLEAN_TYPE, record("test.SideForBacon", asList(
                property("name", STRING_TYPE),
                property("tasteIndex", INTEGER_TYPE),
                property("pairsWithBacon?", BOOLEAN_TYPE)
            )))))
        ));
    }

    @Test
    public void shouldCreateRecordWithPositionalArguments() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: snacks.lang.String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "waffles = SideForBacon 'Waffles' 10 True"
        );
        assertThat(typeOf("test.waffles", EXPRESSION), equalTo(record("test.SideForBacon", asList(
            property("name", STRING_TYPE),
            property("tasteIndex", INTEGER_TYPE),
            property("pairsWithBacon?", BOOLEAN_TYPE)
        ))));
    }

    @Test
    public void appliedConstructorShouldHaveRecordType() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}",
            "waffles = SideForBacon { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }"
        );
        assertThat(typeOf("test.waffles"), equalTo(record("test.SideForBacon", asList(
            property("name", STRING_TYPE),
            property("tasteIndex", INTEGER_TYPE),
            property("pairsWithBacon?", BOOLEAN_TYPE)
        ))));
    }

    @Test
    public void shouldAcceptRecordTypeAsArgument() {
        translate(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? = (x:SideForBacon) -> x.pairsWithBacon?"
        );
        assertThat(typeOf("test.bacon?"), equalTo(func(
            record("test.SideForBacon", asList(
                property("name", STRING_TYPE),
                property("tasteIndex", INTEGER_TYPE),
                property("pairsWithBacon?", BOOLEAN_TYPE)
            )),
            BOOLEAN_TYPE
        )));
    }

    @Test
    public void shouldCreateRecursiveType() {
        translate("data Tree = Leaf | Node Integer Tree Tree");
        assertThat(typeOf("test.Tree", TYPE), equalTo(algebraic("test.Tree", asList(
            simple("test.Leaf"),
            record("test.Node", asList(
                property("_0", simple("snacks.lang.Integer")),
                property("_1", recur("test.Tree")),
                property("_2", recur("test.Tree"))
            ))
        ))));
    }

    @Test
    public void shouldCreateConstantWhenConstructorTakesNoArguments() {
        translate("data Tree = Leaf | Node Integer Tree Tree");
        assertThat(typeOf("test.Leaf", TYPE), equalTo(simple("test.Leaf")));
        assertThat(typeOf("test.Leaf", EXPRESSION), equalTo(typeOf("test.Leaf", TYPE)));
    }

    @Test
    public void shouldCreateNamedTupleIfConstructorTakesPositionalArguments() {
        Type treeType = algebraic("test.Tree", asList(
            simple("test.Leaf"),
            recur("test.Node")
        ));
        Type nodeType = record("test.Node", asList(
            property("_0", simple("snacks.lang.Integer")),
            property("_1", treeType),
            property("_2", treeType)
        ));
        translate("data Tree = Leaf | Node Integer Tree Tree");
        assertThat(typeOf("test.Node", TYPE), equalTo(nodeType));
        assertThat(typeOf("test.Node", EXPRESSION), equalTo(
            func(INTEGER_TYPE, func(treeType, func(treeType, nodeType)))
        ));
    }

    @Test
    public void shouldCreateParameterizedType() {
        translate(
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "node = Node 'Waffles' Leaf Leaf"
        );
        assertThat(typeOf("test.node"), equalTo(record("test.Node", asList(
            property("_0", STRING_TYPE),
            property("_1", algebraic("test.Tree", asList(STRING_TYPE), asList(
                simple("test.Leaf"),
                recur("test.Node", asList(vtype(STRING_TYPE)))
            ))),
            property("_2", algebraic("test.Tree", asList(STRING_TYPE), asList(
                simple("test.Leaf"),
                recur("test.Node", asList(vtype(STRING_TYPE)))
            )))
        ))));
    }

    @Ignore("Type discrepancy with assignments")
    @Test
    public void shouldTranslatePatternMatching() {
        AstNode hurl = hurl(constant("Got nothin!"));
        Type a = vtype("test.Maybe#a");
        hurl.getType().bind(a);
        Type just = record("test.Just", asList(property("_0", a)));
        Type nothing = simple("test.Nothing");
        Type maybe = algebraic("test.Maybe", asList(a), asList(nothing, just));
        String arg0 = "#snacks#~patternArg0";
        String arg1 = "#snacks#~patternArg1";
        Collection<NamedNode> nodes = translate(
            "data Maybe a = Nothing | Just a",
            "perhaps? :: Maybe a -> a -> a",
            "perhaps? = ?(Just value, _) -> value",
            "perhaps? = ?(Nothing, default) -> default"
        );
        assertThat(nodes, defines(declaration("test.perhaps?", patterns(func(func(maybe, a), a), asList(
            pattern(
                asList(
                    matchConstructor(
                        reference(vl(arg0), just),
                        asList(var("value", access(reference(vl(arg0), just), "_0", a)))
                    ),
                    nop()
                ),
                reference(vl("value"), a)
            ),
            pattern(
                asList(
                    matchConstant(reference(vl(arg0), nothing), reference(dl("test.Nothing"), nothing)),
                    var("default", reference(vl(arg1), a))
                ),
                reference(vl("default"), a)
            )
        )))));
    }

    @Test
    public void shouldTranslateRecordPattern() {
        Type type = record("test.BreakfastItem", asList(
            property("name", STRING_TYPE),
            property("tasteIndex", INTEGER_TYPE),
            property("pairsWithBacon?", BOOLEAN_TYPE)
        ));
        Reference argument = reference(vl("#snacks#~patternArg0"), type);
        Collection<NamedNode> nodes = translate(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? :: BreakfastItem -> Boolean",
            "bacon? = ?(BreakfastItem { pairsWithBacon? = x }) -> x"
        );
        assertThat(nodes, defines(declaration("test.bacon?", patterns(func(type, BOOLEAN_TYPE), asList(
            pattern(
                asList(matchConstructor(argument, asList(
                    var("x", access(argument, "pairsWithBacon?", BOOLEAN_TYPE))
                ))),
                reference(vl("x"), BOOLEAN_TYPE)
            )
        )))));
    }

    @Test(expected = TypeException.class)
    public void shouldNotCreateTreeWithIncorrectData() {
        translate(
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "tree = Node 1 (Node 'Waffles' Leaf Leaf) Leaf"
        );
    }

    @Test
    public void shouldUseQuotedFunctionAsOperator() {
        Collection<NamedNode> nodes = translate(
            "add :: Integer -> Integer -> Integer",
            "add = (x y) -> x + y",
            "example = 2 `add` 3"
        );
        assertThat(nodes, defines(declaration("test.example", expression(
            apply(
                apply(
                    reference(dl("test.add"), func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE))),
                    constant(2),
                    func(INTEGER_TYPE, INTEGER_TYPE)
                ),
                constant(3),
                INTEGER_TYPE
            )
        ))));
    }

    @Test
    public void shouldUseSymbolFunctionAsOperator() {
        Collection<NamedNode> nodes = translate(
            "(~>) :: Integer -> Integer -> Integer",
            "(~>) = (x y) -> x ** y",
            "example = 2 ~> 3"
        );
        assertThat(nodes, defines(declaration("test.example", expression(
            apply(
                apply(
                    reference(dl("test.~>"), func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE))),
                    constant(2),
                    func(INTEGER_TYPE, INTEGER_TYPE)
                ),
                constant(3),
                INTEGER_TYPE
            )
        ))));
    }

    private void define(String name, Type type) {
        environment.define(reference(new DeclarationLocator("test.example." + name, EXPRESSION), type));
    }

    private Locator dl(String name) {
        return new DeclarationLocator(name);
    }

    private Collection<NamedNode> translate(String... inputs) {
        return CompilerUtil.translate(environment, inputs);
    }

    private Type typeOf(String qualifiedName, SnackKind kind) {
        return environment.getReference(new DeclarationLocator(qualifiedName, kind)).getType();
    }

    private Type typeOf(String qualifiedName) {
        return environment.getReference(new DeclarationLocator(qualifiedName)).getType();
    }

    private Locator vl(String name) {
        return new VariableLocator(name);
    }

    private Type vtype(String name) {
        return new VariableType(name);
    }

    private Type vtype(Type type) {
        return new VariableType(type);
    }
}
