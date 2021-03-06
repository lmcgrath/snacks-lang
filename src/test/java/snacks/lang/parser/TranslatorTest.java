package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.*;
import static snacks.lang.Types.func;
import static snacks.lang.Types.record;
import static snacks.lang.ast.AstFactory.*;
import static snacks.lang.parser.TranslatorMatcher.defines;

import java.util.Collection;
import org.junit.Test;
import snacks.lang.Type;
import snacks.lang.ast.DeclarationLocator;
import snacks.lang.ast.NamedNode;

public class TranslatorTest extends AbstractTranslatorTest {

    @Test
    public void shouldResolveTypeOfPlusWithIntegers() {
        translate("example = 2 + 2");
        assertThat(typeOf("test.example"), equalTo(integerType()));
    }

    @Test
    public void shouldResolveTypeOfPlusWithInteger() {
        translate("example = (+) 2");
        assertThat(typeOf("test.example"), equalTo(union(
            func(stringType(), stringType()),
            func(doubleType(), doubleType()),
            func(integerType(), integerType())
        )));
    }

    @Test
    public void shouldResolveTypeOfExpressionUsingPossibleTypes() {
        translate(
            "partial = (+) 2",
            "example = partial 'bananas'"
        );
        assertThat(typeOf("test.example"), equalTo(stringType()));
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
                ref(new DeclarationLocator("snacks.lang.+")),
                constant(2),
                union(
                    func(stringType(), stringType()),
                    func(doubleType(), doubleType()),
                    func(integerType(), integerType())
                )
            ),
            reference(new DeclarationLocator("test.value"), stringType()),
            stringType()
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
        assertThat(typeOf("test.example"), equalTo(integerType()));
    }

    @Test
    public void shouldBeAbleToAliasImport() {
        Type var = createVariable();
        define("identity", func(var, var));
        translate(
            "import test.example.identity as id",
            "example = id 12"
        );
        assertThat(typeOf("test.example"), equalTo(integerType()));
    }

    @Test
    public void shouldBeAbleToImportUsingFrom() {
        Type var = createVariable();
        define("identity", func(var, var));
        translate(
            "from test.example import identity",
            "example = identity 12"
        );
        assertThat(typeOf("test.example"), equalTo(integerType()));
    }

    @Test
    public void shouldBeAbleToAliasImportUsingFrom() {
        Type var = createVariable();
        define("identity", func(var, var));
        translate(
            "from test.example import identity as id",
            "example = id 12"
        );
        assertThat(typeOf("test.example"), equalTo(integerType()));
    }

    @Test
    public void shouldTranslateTypedFunction() {
        translate("double = (x:Integer):Integer -> x * 2");
        assertThat(typeOf("test.double"), equalTo(func(integerType(), integerType())));
    }

    @Test(expected = TypeException.class)
    public void shouldNotApplyToDouble() {
        translate("double = (x:Integer -> x * 2) 2.2");
    }

    @Test
    public void shouldTranslateUntypedFunction() {
        translate("double = (x) -> x * 2");
        assertThat(typeOf("test.double"), equalTo(union(
            func(integerType(), integerType()),
            func(doubleType(), doubleType()),
            func(stringType(), stringType())
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
        assertThat(typeOf("test.multiply"), equalTo(func(stringType(), func(integerType(), stringType()))));
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
        assertThat(typeOf("test.waffles"), equalTo(integerType()));
    }

    @Test
    public void shouldTranslateInstantiable() {
        translate("answer = () -> 42");
        assertThat(typeOf("test.answer"), equalTo(func(voidType(), integerType())));
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
        assertThat(typeOf("test.example"), equalTo(func(voidType(), stringType())));
    }

    @Test
    public void mainShouldBeVoidToVoid() {
        translate(
            "speak = () -> say 'Woof'",
            "main = () -> speak ()"
        );
        assertThat(typeOf("test.main"), equalTo(func(voidType(), voidType())));
    }

    @Test
    public void shouldTranslateUntypedThreeArgFunction() {
        translate("volume = (x y z) -> x * y * z");
        assertThat(typeOf("test.volume").decompose().size(), equalTo(9));
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
        assertThat(typeOf("test.test"), equalTo(func(voidType(), integerType())));
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
        assertThat(typeOf("test.example"), equalTo(integerType()));
    }

    @Test
    public void shouldSpecifySignature() {
        translate(
            "addIntegers :: Integer -> Integer -> Integer",
            "addIntegers = (x y) -> x + y"
        );
        assertThat(typeOf("test.addIntegers"), equalTo(func(integerType(), func(integerType(), integerType()))));
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
            "main = () -> say $ string SideForBacon {",
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
            "main = () -> say $ string SideForBacon {",
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
            "main = () -> say $ string SideForBacon {",
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
                property("name", stringType()),
                property("tasteIndex", integerType()),
                property("pairsWithBacon?", booleanType())
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
            property("name", stringType()),
            property("tasteIndex", integerType()),
            property("pairsWithBacon?", booleanType())
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
            property("name", stringType()),
            property("tasteIndex", integerType()),
            property("pairsWithBacon?", booleanType())
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
            property("name", stringType()),
            property("tasteIndex", integerType()),
            property("pairsWithBacon?", booleanType())
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
            func(stringType(), func(integerType(), func(booleanType(), record("test.SideForBacon", asList(
                property("name", stringType()),
                property("tasteIndex", integerType()),
                property("pairsWithBacon?", booleanType())
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
            property("name", stringType()),
            property("tasteIndex", integerType()),
            property("pairsWithBacon?", booleanType())
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
            property("name", stringType()),
            property("tasteIndex", integerType()),
            property("pairsWithBacon?", booleanType())
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
                property("name", stringType()),
                property("tasteIndex", integerType()),
                property("pairsWithBacon?", booleanType())
            )),
            booleanType()
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
            func(integerType(), func(treeType, func(treeType, nodeType)))
        ));
    }

    @Test
    public void shouldCreateParameterizedType() {
        translate(
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "node = Node 'Waffles' Leaf Leaf"
        );
        assertThat(typeOf("test.node"), equalTo(record("test.Node", asList(
            property("_0", stringType()),
            property("_1", algebraic("test.Tree", asList(stringType()), asList(
                simple("test.Leaf"),
                recur("test.Node", asList(vtype(stringType())))
            ))),
            property("_2", algebraic("test.Tree", asList(stringType()), asList(
                simple("test.Leaf"),
                recur("test.Node", asList(vtype(stringType())))
            )))
        ))));
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
                    reference(dl("test.add"), func(integerType(), func(integerType(), integerType()))),
                    constant(2),
                    func(integerType(), integerType())
                ),
                constant(3),
                integerType()
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
                    reference(dl("test.~>"), func(integerType(), func(integerType(), integerType()))),
                    constant(2),
                    func(integerType(), integerType())
                ),
                constant(3),
                integerType()
            )
        ))));
    }

    @Test(expected = TypeException.class)
    public void shouldNotCreateHeterogeneousList() {
        translate("test = [1, 2, 'Waffles']");
    }
}
