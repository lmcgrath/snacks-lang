package snacks.lang.runtime;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import snacks.lang.SnacksException;
import snacks.lang.util.SnacksTest;

public class RuntimeTest extends SnacksTest {

    @Test
    public void shouldSayHello() {
        run("main = () -> say 'Hello World!'");
        verifyOut("Hello World!");
    }

    @Test
    public void shouldSpeakThroughReference() {
        run(
            "speak = () -> say 'Woof'",
            "main = () -> speak()"
        );
        verifyOut("Woof");
    }

    @Test
    public void shouldMultiplyInteger() {
        run("main = () -> say $ 2 * 3");
        verifyOut(6);
    }

    @Test
    public void shouldSayConstantReference() {
        run(
            "bananas = 2 + 2",
            "main = () -> say bananas"
        );
        verifyOut(4);
    }

    @Test
    public void shouldSayMultiplyFunction() {
        run(
            "multiply = (x) -> x * 2",
            "main = () -> say $ multiply 4"
        );
        verifyOut(8);
    }

    @Test
    public void shouldSayMultiplyWithTwoArguments() {
        run(
            "main = () -> say $ 3 * 5"
        );
        verifyOut(15);
    }

    @Test
    public void shouldSayTripleWithThreeArguments() {
        run(
            "triple = (x y z) -> x * y * z",
            "main = () -> say $ triple 9 3 348"
        );
        verifyOut(9396);
    }

    @Test
    public void shouldSayQuadrupleWithFourArguments() {
        run(
            "quadruple = (w x y z) -> w * x * y * z",
            "main = () -> say $ quadruple 3 3 3 3"
        );
        verifyOut(81);
    }

    @Test
    public void shouldCompileBlock() {
        run(
            "main = {",
            "    say 'Hello'",
            "    say 'World!'",
            "}"
        );
        verifyOut("Hello");
        verifyOut("World!");
    }

    @Test
    public void shouldReferenceBlock() {
        run(
            "waffles = {",
            "    say $ (x y -> x + y) 2 3",
            "    say $ 4 * 5",
            "}",
            "main = () -> waffles()"
        );
        verifyOut(5);
        verifyOut(20);
    }

    @Test
    public void shouldStoreVariables() {
        run(
            "main = {",
            "    var x = 12",
            "    var y = 3",
            "    say $ x * y",
            "}"
        );
        verifyOut(36);
    }

    @Test
    public void shouldReturnVariables() {
        run(
            "triple = { x ->",
            "    var y = 3",
            "    return x * y",
            "}",
            "main = () -> say $ triple 12"
        );
        verifyOut(36);
    }

    @Test
    public void shouldAllowDeadCodeAfterReturn() {
        run(
            "triple = { x ->",
            "    var y = 3",
            "    return x * y",
            "    say 'can\\'t touch this'",
            "}",
            "main = () -> say $ triple 12"
        );
        verifyOut(36);
        verifyNever("can't touch this");
    }

    @Test
    public void shouldReturnClosureFromFunction() {
        run(
            "closure = (x) -> {",
            "    var z = x + 1",
            "    (y) -> z * y",
            "}",
            "main = () -> say $ closure 4 31"
        );
        verifyOut(155);
    }

    @Test
    public void shouldReferenceVariablesInParentScopes() {
        run(
            "closure = (x) -> {",
            "    var y = x * 2",
            "    var z = x + 1",
            "    return (w) -> {",
            "        var v = w + 3",
            "        return (u) -> \"x = #{x}; y = #{y}; z = #{z}; w = #{w}; v = #{v}; u = #{u}\"",
            "    }",
            "}",
            "main = () -> say $ closure 5 8 12"
        );
        verifyOut("x = 5; y = 10; z = 6; w = 8; v = 11; u = 12");
    }

    @Test
    public void shouldCompileMultiLineString() {
        run(
            "value = 'waffles ' * 3",
            "multiline = \"\"\"",
            "I like",
            " #{value}and bananas",
            "\"\"\"",
            "main = () -> say multiline"
        );
        verifyOut("I like\n waffles waffles waffles and bananas\n");
    }

    @Test
    public void shouldPassFunctionIntoFunction() {
        run(
            "operate = (op) -> op 2 4",
            "main = {",
            "    say $ operate (+)",
            "    say $ operate (*)",
            "}"
        );
        verifyOut(6);
        verifyOut(8);
    }

    @Test
    public void shouldOverridePlus() {
        run(
            "main = {",
            "    var (+) = (x y) -> 'sneaky ninja (='",
            "    say $ 3 + 24",
            "}"
        );
        verifyOut("sneaky ninja (=");
    }

    @Test
    public void shouldConcatenateStrings() {
        run("main = () -> say $ 'Hello ' + 'World!'");
        verifyOut("Hello World!");
    }

    @Test
    public void shouldConcatenateStringToInteger() {
        run("main = () -> say $ 'Answer = ' + 4 + 2");
        verifyOut("Answer = 42");
    }

    @Test
    public void shouldMultiplyString() {
        run("main = () -> say $ 'waffles ' * 3");
        verifyOut("waffles waffles waffles ");
    }

    @Test
    public void shouldCompileConditional() {
        run(
            "booleanizer = (name value) -> {",
            "    if value:",
            "        say \"#{name} is true!\"",
            "    else if value is 'oranges':",
            "        say 'We have oranges!'",
            "    else",
            "        say \"#{name} is false!\"",
            "    end",
            "}",
            "main = {",
            "    booleanizer 'waffles' True",
            "    booleanizer 'bananas' False",
            "    booleanizer 'monkeys' 'oranges'",
            "}"
        );
        verifyOut("waffles is true!");
        verifyOut("bananas is false!");
        verifyOut("We have oranges!");
    }

    @Test
    public void shouldCompileVariableReassignment() {
        run(
            "main = {",
            "    var x = 'apples'",
            "    var y",
            "    x = 'waffles'",
            "    y = 'bananas'",
            "    say \"#{x} and #{y}\"",
            "}"
        );
        verifyOut("waffles and bananas");
    }

    @Test
    public void shouldNotBeTrueWhenNotted() {
        run(
            "main = () ->",
            "    if not True:",
            "        say 'It\\'s not true!'",
            "    else",
            "        say 'It\\'s true!'",
            "    end"
        );
        verifyOut("It\'s true!");
    }

    @Test
    public void shouldCompileHappyExceptional() {
        run(
            "main = () -> begin",
            "    say 'oops'",
            "embrace e ->",
            "    say 'got it!'",
            "ensure",
            "    say 'cleaning stuff up'",
            "end"
        );
        verifyOut("oops");
        verifyOut("cleaning stuff up");
        verifyNever("got it!");
    }

    @Test
    public void shouldCompileSadExceptional() {
        run(
            "main = () -> begin",
            "    hurl 'oops'",
            "    say 'oops, did not oops'",
            "embrace e ->",
            "    say 'got it!'",
            "ensure",
            "    say 'cleaning stuff up'",
            "end"
        );
        verifyNever("oops, did not oops");
        verifyOut("got it!");
        verifyOut("cleaning stuff up");
    }

    @Test
    public void shouldRethrowException() {
        try {
            run(
                "main = () -> begin",
                "    hurl 'oops'",
                "embrace e ->",
                "    hurl e",
                "    say 'oops, did not throw'",
                "ensure",
                "    say 'cleaning stuff up'",
                "end"
            );
            fail("Did not throw");
        } catch (SnacksException exception) {
            assertThat(exception.getMessage(), equalTo("oops"));
            verifyNever("oops, did not throw");
            verifyOut("cleaning stuff up");
        }
    }

    @Test
    public void shouldCompileNestedExceptional() {
        run(
            "main = () -> begin",
            "    say 'beginning!'",
            "    begin",
            "        hurl 'oops!'",
            "    ensure",
            "        say 'here it comes!'",
            "    end",
            "embrace x ->",
            "    say 'got it!'",
            "end"
        );
        verifyOut("here it comes!");
        verifyOut("got it!");
    }

    @Test
    public void shouldCompileModulo() {
        run("main = () -> say $ 5 % 2");
        verifyOut(1);
    }

    @Test
    public void shouldCompileDivide() {
        run("main = () -> say $ 6 / 2");
        verifyOut(3);
    }

    @Test
    public void shouldCompileMinus() {
        run("main = () -> say $ 5 - 2");
        verifyOut(3);
    }

    @Test
    public void shouldCompileNegative() {
        run("main = () -> assert $ -3 == 3 * -1");
    }

    @Test
    public void shouldCompilePositive() {
        run("main = () -> assert $ +3 == 3");
    }

    @Test
    public void shouldCompileNegativeNegative() {
        run("main = () -> assert $ -(- 3) == 3");
    }

    @Test
    public void shouldCompileIfWithoutElse() {
        run(
            "main = {",
            "    var x = 10",
            "    if x >= 10:",
            "        say 'got it!'",
            "    end",
            "}"
        );
        verifyOut("got it!");
    }

    @Test
    public void shouldCompileMultiIfWithoutElse() {
        run(
            "main = {",
            "    var x = 9",
            "    if x >= 10:",
            "        say '10 or bigger'",
            "    else if x >= 9:",
            "        say '9 or bigger'",
            "    end",
            "}"
        );
        verifyOut("9 or bigger");
    }

    @Test
    public void shouldCompileSequentialAssign() {
        run(
            "main = {",
            "    var x",
            "    var y",
            "    x = y = 3",
            "    assert $ x == 3",
            "    assert $ y == 3",
            "}"
        );
    }

    @Test
    public void shouldCompileLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    x = x + 1 while x < 10",
            "    var expected = 10",
            "    assert $ x is expected",
            "}"
        );
    }

    @Test
    public void shouldBreakLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    while True:",
            "        x = x + 1",
            "        break if x > 10",
            "    end",
            "    var expected = 11",
            "    assert $ x is expected",
            "}"
        );
    }

    @Test
    public void shouldContinueLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    var last = 0",
            "    while x < 8:",
            "        x = x + 1",
            "        continue if x % 2 == 0",
            "        say \"x is #{x}\"",
            "    end",
            "}"
        );
        verifyNever("x is 0");
        verifyNever("x is 2");
        verifyNever("x is 4");
        verifyNever("x is 6");
        verifyOut("x is 1");
        verifyOut("x is 3");
        verifyOut("x is 5");
        verifyOut("x is 7");
    }

    @Test
    public void shouldCompileNestedLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    var total = 0",
            "    while x < 10:",
            "        var y = 0",
            "        while y < 10:",
            "            y = y + 1",
            "            total = total + 1",
            "        end",
            "        x = x + 1",
            "    end",
            "    assert $ total == 100",
            "}"
        );
    }

    @Test
    public void shouldBeAbleToBreakLoopFromWithinEmbrace() {
        run(
            "main = {",
            "    var counter = 0",
            "    while counter < 10:",
            "        begin",
            "            counter = counter + 1",
            "            hurl 'oops' if counter > 8",
            "        embrace error ->",
            "            break",
            "        end",
            "    end",
            "    assert $ counter == 9",
            "}"
        );
    }

    @Test
    public void shouldCreateTuple() {
        run("main = () -> assert $ string ('waffles', 10, True) == '(waffles, 10, true)'");
    }

    @Test
    public void shouldAccessSecondMemberOfTuple() {
        run("main = () -> assert $ ('waffles', 10, True)._1 == 10");
    }

    @Test
    public void shouldSpecifySignature() {
        run(
            "addIntegers :: Integer -> Integer -> Integer",
            "main = () -> assert $ addIntegers 2 4 == 6",
            "addIntegers = (x y) -> x + y"
        );
    }

    @Test
    public void shouldPassTupleAsArgument() {
        run(
            "something :: (String, Boolean, Integer) -> ()",
            "main = () -> something ('waffles', True, 3)",
            "something = (x) -> assert $ string x == '(waffles, true, 3)'"
        );
    }

    @Test
    public void shouldReturnTupleAsResult() {
        run(
            "pair :: String -> Integer -> (Integer, String)",
            "main = () -> assert $ (pair 'waffles' 2) == (2, 'waffles')",
            "pair = (x y) -> (y, x)"
        );
    }

    @Test
    public void shouldCompileCustomOperator() {
        run(
            "<&> :: Integer -> Integer -> Integer",
            "<&> = (x y) -> x + y",
            "<&> infix left 3",
            "main = () -> assert $ (3 <&> 5 * 2) == 13"
        );
    }

    @Test
    public void shouldResolveTypesWithFullyQualifiedNames() {
        run(
            "something :: (snacks.lang.String, snacks.lang.Boolean, snacks.lang.Integer) -> ()",
            "main = () -> something ('waffles', True, 3)",
            "something = (x) -> say $ string x"
        );
        verifyOut("(waffles, true, 3)");
    }

    @Test
    public void shouldBooleanizeLogic() {
        run(
            "main = {",
            "    assert $ not (3 == 4 or 5 >= 5 and True is False)",
            "    assert $ 3 == 4 or 5 >= 5 and 'waffles' is 'waffles'",
            "}"
        );
    }

    @Test
    public void shouldCreatePrefixOperator() {
        run(
            "?% :: Boolean -> String",
            "?% affix right 10",
            "main = {",
            "    assert $ ?% True == 'Woot!'",
            "    assert $ ?% False == 'Aww...'",
            "}",
            "?% = (x) -> if x is True: 'Woot!' else 'Aww...' end"
        );
    }

    @Test
    public void shouldCompileRecord() {
        run(
            "data BreakfastItem = SideForBacon {",
            "    name: snacks.lang.String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ string SideForBacon {",
            "    name = 'Waffles',",
            "    tasteIndex = 10,",
            "    pairsWithBacon? = True",
            "}"
        );
        verifyOut("SideForBacon{name=Waffles, tasteIndex=10, pairsWithBacon?=true}");
    }

    @Test
    public void shouldCompileSingularRecord() {
        run(
            "data BreakfastItem = {",
            "    name: snacks.lang.String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ string BreakfastItem {",
            "    name = 'Waffles',",
            "    tasteIndex = 10,",
            "    pairsWithBacon? = True",
            "}"
        );
        verifyOut("BreakfastItem{name=Waffles, tasteIndex=10, pairsWithBacon?=true}");
    }

    @Test
    public void shouldCreateRecordWithPositionalArguments() {
        run(
            "data BreakfastItem = SideForBacon {",
            "    name: snacks.lang.String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ string $ SideForBacon 'Waffles' 10 True"
        );
        verifyOut("SideForBacon{name=Waffles, tasteIndex=10, pairsWithBacon?=true}");
    }

    @Test
    public void shouldReferenceRecordProperty() {
        run(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "waffles = SideForBacon { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }",
            "main = {",
            "    assert waffles.pairsWithBacon?",
            "    assert $ waffles.name + 10 == 'Waffles' + waffles.tasteIndex",
            "}"
        );
    }

    @Test
    public void shouldAcceptRecordAsArgument() {
        run(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? = (x:SideForBacon) -> x.pairsWithBacon?",
            "waffles = SideForBacon { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }",
            "main = () -> assert $ bacon? waffles"
        );
    }

    @Test
    public void recordWithoutConstructorShouldDefineConstructorWithNameOfType() {
        run(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean",
            "}",
            "bacon? = (x:BreakfastItem) -> x.pairsWithBacon?",
            "waffles = BreakfastItem { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }",
            "main = () -> assert $ bacon? waffles"
        );
    }

    @Test
    public void shouldCreateRecursiveNamedTuple() {
        run(
            "data Tree = Leaf | Node Integer Tree Tree",
            "main = () -> say $ string $ Node 3 Leaf Leaf"
        );
        verifyOut("Node(3, Leaf, Leaf)");
    }

    @Test
    public void shouldCreateParameterizedType() {
        run(
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "main = () -> say $ string $ Node 'Waffles' Leaf Leaf"
        );
        verifyOut("Node(Waffles, Leaf, Leaf)");
    }

    @Test
    public void shouldDeconstructRecord() {
        run(
            "data BreakfastItem = SideForBacon {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? :: BreakfastItem -> Boolean",
            "bacon? = ?(SideForBacon { pairsWithBacon? = x }) -> x",
            "waffles = SideForBacon { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }",
            "main = () -> assert $ bacon? waffles"
        );
    }

    @Test
    public void shouldDeconstructJust() {
        run(
            "data Maybe a = Nothing | Just a",
            "require :: Maybe a -> a",
            "main = () -> assert $ require (Just 1) == 1",
            "require = ?(Just value) -> value",
            "require = ?(Nothing) -> hurl 'Got nothin!'"
        );
    }

    @Test(expected = SnacksException.class)
    public void shouldHurlNothing() {
        run(
            "data Maybe a = Nothing | Just a",
            "require :: Maybe a -> a",
            "main = () -> require Nothing",
            "require = ?(Just value) -> value",
            "require = ?(Nothing) -> hurl 'Got nothin!'"
        );
    }

    @Ignore("WIP")
    @Test
    public void shouldPatternMatchMultipleArguments() {
        run(
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "leftTree = Node 1 (Node 2 (Node 3 Leaf Leaf) (Node 4 Leaf Leaf)) Leaf",
            "rightTree = Node 1 (Node 2 Leaf (Node 4 Leaf Leaf)) Leaf",
            "eq :: Tree a -> Tree a -> Boolean",
            "main = () -> assert $ not (leftTree `eq` rightTree)",
            "eq = ?(Leaf, Leaf) -> True",
            "eq = ?(Node x l1 r1, Node y l2 r2) -> x == x and l1 `eq` r1 and l2 `eq` r2",
            "eq = ?(_, _) -> False"
        );
    }

    @Test
    public void shouldDefineSignatureWithGenericArguments() {
        run(
            "<$ :: (a -> b) -> a -> b",
            "<$ infix right 1",
            "main = () -> assert <$ 1 + 2 == 3",
            "<$ = (a b) -> a b"
        );
    }

    @Test
    public void shouldReturnInvokableFromFunction() {
        run(
            "test = (x y) -> () -> x + y",
            "main = () -> assert $ test 1 2 () == 3"
        );
    }

    @Test
    public void shouldUseAndAsFunction() {
        run("main = () -> assert $ not ((and) True False)");
    }

    @Test
    public void shouldShortCircuitLogicalAnd() {
        run(
            "speakTruth = (message) -> { say message; True }",
            "speakLies = (message) -> { say message; False }",
            "main = () -> assert $ !(speakLies 'waffles' and speakTruth 'toast')"
        );
        verifyOut("waffles");
        verifyNever("toast");
    }

    @Test
    public void shouldShortCircuitLogicalOr() {
        run(
            "speakTruth = (message) -> { say message; True }",
            "main = () -> assert $ speakTruth 'waffles' or speakTruth 'toast'"
        );
        verifyOut("waffles");
        verifyNever("toast");
    }

    @Test
    public void shouldExecuteBothSidesOfLogicalAnd_whenLeftSideIsTrue() {
        run(
            "speakTruth = (message) -> { say message; True }",
            "main = () -> assert $ speakTruth 'waffles' and speakTruth 'toast'"
        );
        verifyOut("waffles");
        verifyOut("toast");
    }

    @Test
    public void shouldExecuteBothSidesOfLogicalOr_whenLeftSideIsFalse() {
        run(
            "speakTruth = (message) -> { say message; True }",
            "speakLies = (message) -> { say message; False }",
            "main = () -> assert $ speakLies 'waffles' or speakTruth 'toast'"
        );
        verifyOut("waffles");
        verifyOut("toast");
    }

    @Test
    public void shouldTraverseTree() {
        run(
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "size :: Tree a -> Integer",
            "tree = Node 1 (Node 2 (Node 3 Leaf Leaf) (Node 4 Leaf Leaf)) Leaf",
            "main = () -> assert $ size tree == 4",
            "size = ?(Leaf) -> 0",
            "size = ?(Node _ left right) -> 1 + size left + size right"
        );
    }

    @Test
    public void shouldUseFunctionAsOperator() {
        run(
            "add :: Integer -> Integer -> Integer",
            "main = () -> assert $ 2 `add` 3 == 5",
            "add = (a b) -> a + b"
        );
    }

    @Test
    public void shouldCreateList() {
        run("main = () -> assert $ string [1, 2, 3] == '[1, 2, 3]'");
    }

    @Test
    public void shouldCreateEmptyList() {
        run("main = () -> assert $ string [] == '[]'");
    }

    @Test
    public void shouldCreateListOfTuples() {
        run("main = () -> assert $ string [(1, 2), (3, 4)] == '[(1, 2), (3, 4)]'");
    }

    @Test
    public void shouldCreateStringSymbol() {
        run("main = () -> assert $ :'waffles' == :waffles");
    }

    @Test
    public void shouldCreateInterpolatedSymbol() {
        run(
            "waffles = 'waffles'",
            "main = () -> assert $ :waffles == :\"#{waffles}\""
        );
    }

    @Ignore("WIP")
    @Test
    public void shouldCreateProtocol() {
        run(
            "protocol Equitable a where",
            "    (==) :: a -> a -> Boolean",
            "    (!=) :: a -> a -> Boolean",
            "    (!=) = (x y) -> not $ x == y",
            "end",
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "main = () -> {",
            "    assert $ (Node 3 Leaf Leaf) == (Node 3 Leaf Leaf)",
            "    assert $ (Node 4 Leaf Leaf) != (Node 4 (Node 1 Leaf Leaf) Leaf)",
            "}",
            "implement Equitable (Tree a) where",
            "    (==) = ?(Leaf, Leaf) -> True",
            "    (==) = ?(Node x l1 r1, Node y l2 r2) -> x == y and l1 == l2 and r1 == r2",
            "    (==) = ?(_, _) -> False",
            "end"
        );
    }
}
