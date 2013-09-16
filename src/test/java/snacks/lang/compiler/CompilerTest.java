package snacks.lang.compiler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static snacks.lang.parser.CompilerUtil.translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import snacks.lang.Invokable;
import snacks.lang.SnacksException;
import snacks.lang.SnacksLoader;

public class CompilerTest {

    @Rule
    public final OutResource out;
    private Compiler compiler;
    private SnacksLoader loader;

    public CompilerTest() {
        out = new OutResource();
    }

    @Before
    public void setUp() {
        loader = new SnacksLoader();
        compiler = new Compiler(loader);
    }

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
            "    say $ operate `+`",
            "    say $ operate `*`",
            "}"
        );
        verifyOut(6);
        verifyOut(8);
    }

    @Test
    public void shouldOverridePlus() {
        run(
            "main = {",
            "    var `+` = (x y) -> 'sneaky ninja (='",
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
            "    if value",
            "        say \"#{name} is true!\"",
            "    else if value is 'oranges'",
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
            "    if not True",
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
        run("main = () -> assert $ -(-3) == 3");
    }

    @Test
    public void shouldCompileIfWithoutElse() {
        run(
            "main = {",
            "    var x = 10",
            "    if x >= 10",
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
            "    if x >= 10",
            "        say '10 or bigger'",
            "    else if x >= 9",
            "        say '9 or bigger'",
            "    end",
            "}"
        );
        verifyOut("9 or bigger" );
    }

    @Test
    public void shouldCompileSequentialAssign() {
        run(
            "main = {",
            "    var x",
            "    var y",
            "    x = y = 3",
            "    assert (x == 3, 'x was not 3')",
            "    assert (y == 3, 'y was not 3')",
            "}"
        );
    }

    @Ignore
    @Test
    public void shouldCompileLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    x = x + 1, while x < 10",
            "    var expected = 10",
            "    assert (x is expected, \"Got #{x}, expected #{expected}\")",
            "}"
        );
    }

    @Test
    public void shouldBreakLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    while True",
            "        x = x + 1",
            "        break, if x > 10",
            "    end",
            "    var expected = 11",
            "    assert (x is expected, \"Got #{x}, expected #{expected}\")",
            "}"
        );
    }

    @Test
    public void shouldContinueLoop() {
        run(
            "main = {",
            "    var x = 0",
            "    var last = 0",
            "    while x < 8",
            "        x = x + 1",
            "        continue, if x % 2 == 0",
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
            "    while x < 10",
            "        var y = 0",
            "        while y < 10",
            "            y = y + 1",
            "            total = total + 1",
            "        end",
            "        x = x + 1",
            "    end",
            "    assert (total == 100, \"Got #{total}, expected 100\")",
            "}"
        );
    }

    @Test
    public void shouldBeAbleToBreakLoopFromWithinEmbrace() {
        run(
            "main = {",
            "    var counter = 0",
            "    while counter < 10",
            "        begin",
            "            counter = counter + 1",
            "            hurl 'oops', if counter > 8",
            "        embrace error ->",
            "            break",
            "        end",
            "    end",
            "    assert (counter == 9, 'Counter was not 9')",
            "}"
        );
    }

    @Test
    public void shouldCreateTuple() {
        run("main = () -> assert $ stringy ('waffles', 10, True) == '(Tuple3 waffles, 10, true)'");
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
            "something = (x) -> assert $ stringy x == '(Tuple3 waffles, true, 3)'"
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
            "infix left 3 <&>",
            "main = () -> assert $ (3 <&> 5 * 2) == 13"
        );
    }

    @Test
    public void shouldResolveTypesWithFullyQualifiedNames() {
        run(
            "something :: (snacks.lang.String, snacks.lang.Boolean, snacks.lang.Integer) -> ()",
            "main = () -> something ('waffles', True, 3)",
            "something = (x) -> say $ stringy x"
        );
        verifyOut("(Tuple3 waffles, true, 3)");
    }

    @Test
    public void shouldBooleanizeLogic() {
        run(
            "main = {",
            "    assert ((3 == 4 or 5 >= 5 and True is False) is False, 'Was not false')",
            "    assert ((3 == 4 or 5 >= 5 and 'waffles' is 'waffles') is True, 'Was not true')",
            "}"
        );
    }

    @Test
    public void shouldCreatePrefixOperator() {
        run(
            "?% :: Boolean -> String",
            "affix right 10 ?%",
            "main = {",
            "    assert $ ?% True == 'Woot!'",
            "    assert $ ?% False == 'Aww...'",
            "}",
            "?% = (x) -> if x is True then 'Woot!' else 'Aww...' end"
        );
    }

    @Test
    public void shouldCompileRecord() {
        run(
            "data BreakfastItem = BreakfastItem {",
            "    name: snacks.lang.String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "main = () -> say $ stringy BreakfastItem {",
            "    name = 'Waffles',",
            "    tasteIndex = 10,",
            "    pairsWithBacon? = True",
            "}"
        );
        verifyOut("BreakfastItem{name=Waffles, pairsWithBacon?=true, tasteIndex=10}");
    }

    @Test
    public void shouldReferenceRecordProperty() {
        run(
            "data BreakfastItem = BreakfastItem {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "waffles = BreakfastItem { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }",
            "main = {",
            "    assert waffles.pairsWithBacon?",
            "    assert $ waffles.name + 10 == 'Waffles' + waffles.tasteIndex",
            "}"
        );
    }

    @Test
    public void shouldAcceptRecordAsArgument() {
        run(
            "data BreakfastItem = BreakfastItem {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? = (x:BreakfastItem) -> x.pairsWithBacon?",
            "waffles = BreakfastItem { name = 'Waffles', tasteIndex = 10, pairsWithBacon? = True }",
            "main = () -> assert $ bacon? waffles"
        );
    }

    private void run(String... inputs) {
        try {
            for (SnackDefinition definition : compiler.compile(translate(inputs))) {
                loader.defineSnack(definition);
                writeClass(new File(definition.getJavaName().replace('.', '/') + ".class"), definition.getBytes());
            }
            ((Invokable) loader.loadClass("test.Main").newInstance()).invoke();
        } catch (ReflectiveOperationException exception) {
            throw new CompileException(exception);
        }
    }

    private void verifyOut(int value) {
        verify(out.getStream()).println(value);
    }

    private void verifyOut(String line) {
        verify(out.getStream()).println(line);
    }

    private void verifyNever(String line) {
        verify(out.getStream(), never()).println(line);
    }

    private void writeClass(File file, byte[] bytes) {
        try {
            if (!file.getParentFile().mkdirs() && !file.getParentFile().exists()) {
                throw new IOException("Failed to mkdirs: " + file.getParentFile());
            }
            try (FileOutputStream output = new FileOutputStream(file)) {
                output.write(bytes);
            }
        } catch (IOException exception) {
            throw new CompileException(exception);
        }
    }
}
