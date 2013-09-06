package snacks.lang.compiler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static snacks.lang.parser.CompilerUtil.translate;

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

    public CompilerTest() {
        out = new OutResource();
    }

    @Before
    public void setUp() {
        compiler = new Compiler(new SnacksLoader());
    }

    @Test
    public void shouldSayHello() throws Exception {
        run("main = () -> say 'Hello World!'");
        verifyOut("Hello World!");
    }

    @Test
    public void shouldSpeakThroughReference() throws Exception {
        run(
            "speak = () -> say 'Woof'",
            "main = () -> speak()"
        );
        verifyOut("Woof");
    }

    @Test
    public void shouldMultiplyInteger() throws Exception {
        run("main = () -> say $ 2 * 3");
        verifyOut(6);
    }

    @Test
    public void shouldSayConstantReference() throws Exception {
        run(
            "bananas = 2 + 2",
            "main = () -> say bananas"
        );
        verifyOut(4);
    }

    @Test
    public void shouldSayMultiplyFunction() throws Exception {
        run(
            "multiply = (x) -> x * 2",
            "main = () -> say $ multiply 4"
        );
        verifyOut(8);
    }

    @Test
    public void shouldSayMultiplyWithTwoArguments() throws Exception {
        run(
            "multiply = (x y) -> x * y",
            "main = () -> say $ multiply 3 5"
        );
        verifyOut(15);
    }

    @Test
    public void shouldSayTripleWithThreeArguments() throws Exception {
        run(
            "triple = (x y z) -> x * y * z",
            "main = () -> say $ triple 9 3 348"
        );
        verifyOut(9396);
    }

    @Test
    public void shouldSayQuadrupleWithFourArguments() throws Exception {
        run(
            "quadruple = (w x y z) -> w * x * y * z",
            "main = () -> say $ quadruple 3 3 3 3"
        );
        verifyOut(81);
    }

    @Test
    public void shouldCompileBlock() throws Exception {
        run(
            "main = {",
            "    say 'Hello'",
            "    say 'World!'",
            "}"
        );
        verifyOut("Hello" );
        verifyOut("World!");
    }

    @Test
    public void shouldReferenceBlock() throws Exception {
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
    public void shouldStoreVariables() throws Exception {
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
    public void shouldReturnVariables() throws Exception {
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
    public void shouldAllowDeadCodeAfterReturn() throws Exception {
        run(
            "triple = { x ->",
            "    var y = 3",
            "    return x * y",
            "    say 'can\\'t touch this'",
            "}",
            "main = () -> say $ triple 12"
        );
        verifyOut(36);
        verifyNever("can't touch this" );
    }

    @Test
    public void shouldReturnClosureFromFunction() throws Exception {
        run(
            "closure = { x ->",
            "    var z = x + 1",
            "    (y) -> z * y",
            "}",
            "main = () -> say $ closure 4 31"
        );
        verifyOut(155);
    }

    @Test
    public void shouldReferenceVariablesInParentScopes() throws Exception {
        run(
            "closure = { x ->",
            "    var y = x * 2",
            "    var z = x + 1",
            "    return { w ->",
            "        var v = w + 3",
            "        return (u) -> \"x = #{x}; y = #{y}; z = #{z}; w = #{w}; v = #{v}; u = #{u}\"",
            "    }",
            "}",
            "main = () -> say $ closure 5 8 12"
        );
        verifyOut("x = 5; y = 10; z = 6; w = 8; v = 11; u = 12");
    }

    @Test
    public void shouldCompileMultiLineString() throws Exception {
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

    @Ignore
    @Test
    public void shouldPassFunctionIntoFunction() throws Exception {
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
    public void shouldOverridePlus() throws Exception {
        run(
            "main = {",
            "    var `+` = (x y) -> 'sneaky ninja (='",
            "    say $ 3 + 24",
            "}"
        );
        verifyOut("sneaky ninja (=");
    }

    @Test
    public void shouldConcatenateStrings() throws Exception {
        run("main = () -> say $ 'Hello ' + 'World!'");
        verifyOut("Hello World!");
    }

    @Test
    public void shouldConcatenateStringToInteger() throws Exception {
        run("main = () -> say $ 'Answer = ' + 4 + 2" );
        verifyOut("Answer = 42");
    }

    @Test
    public void shouldMultiplyString() throws Exception {
        run("main = () -> say $ 'waffles ' * 3" );
        verifyOut("waffles waffles waffles ");
    }

    @Test
    public void shouldCompileConditional() throws Exception {
        run(
            "booleanizer = (name value) ->",
            "    if value",
            "        say \"#{name} is true!\"",
            "    else if value is 'oranges'",
            "        say 'We have oranges!'",
            "    else",
            "        say \"#{name} is false!\"",
            "    end",
            "main = {",
            "    booleanizer 'waffles' True",
            "    booleanizer 'bananas' False",
            "    booleanizer 'monkeys' 'oranges'",
            "}"
        );
        verifyOut("waffles is true!");
        verifyOut("bananas is false!" );
        verifyOut("We have oranges!");
    }

    @Test
    public void shouldCompileVariableReassignment() throws Exception {
        run(
            "main = {",
            "    var x = 'apples'",
            "    var y",
            "    x = 'waffles'",
            "    y = 'bananas'",
            "    say \"#{x} and #{y}\"",
            "}"
        );
        verifyOut("waffles and bananas" );
    }

    @Test
    public void shouldNotBeTrueWhenNotted() throws Exception {
        run(
            "main = () ->",
            "    if not True",
            "        say 'It\\'s not true!'",
            "    else",
            "        say 'It\\'s true!'",
            "    end"
        );
        verifyOut("It\'s true!" );
    }

    @Test
    public void shouldCompileHappyExceptional() throws Exception {
        run(
            "main = () -> begin",
            "    say 'oops'",
            "embrace e ->",
            "    say 'got it!'",
            "ensure",
            "    say 'cleaning stuff up'",
            "end"
        );
        verifyOut("oops" );
        verifyOut("cleaning stuff up" );
        verifyNever("got it!" );
    }

    @Test
    public void shouldCompileSadExceptional() throws Exception {
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
    public void shouldRethrowException() throws Exception {
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
    public void shouldCompileNestedExceptional() throws Exception {
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
        verifyOut("here it comes!" );
        verifyOut("got it!");
    }

    @Test
    public void shouldCompileModulo() throws Exception {
        run("main = () -> say $ 5 % 2" );
        verifyOut(1);
    }

    @Test
    public void shouldCompileDivide() throws Exception {
        run("main = () -> say $ 6 / 2");
        verifyOut(3);
    }

    @Test
    public void shouldCompileMinus() throws Exception {
        run("main = () -> say $ 5 - 2");
        verifyOut(3);
    }

    @Test
    public void shouldCompileNegative() throws Exception {
        run("main = () -> say $ -3");
        verifyOut(-3);
    }

    @Test
    public void shouldCompilePositive() throws Exception {
        run("main = () -> say $ +3");
        verifyOut(3);
    }

    @Test
    public void shouldCompileNegativeNegative() throws Exception {
        run("main = () -> say $ --3");
        verifyOut(3);
    }

    @Test
    public void shouldCompileIfWithoutElse() throws Exception {
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
    public void shouldCompileMultiIfWithoutElse() throws Exception {
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
    public void shouldCompileSequentialAssign() throws Exception {
        run(
            "main = {",
            "    var x",
            "    var y",
            "    x = y = 3",
            "    say \"x = #{x}; y = #{y}\"",
            "}"
        );
        verifyOut("x = 3; y = 3" );
    }

    @Test
    public void shouldCompileLoop() throws Exception {
        run(
            "main = {",
            "    var x = 0",
            "    x += 1, while x < 10",
            "    say x",
            "}"
        );
        verifyOut(10);
    }

    @Test
    public void shouldBreakLoop() throws Exception {
        run(
            "main = {",
            "    var x = 0",
            "    while True",
            "        x += 1",
            "        break, if x > 10",
            "    end",
            "    say \"X is #{x}\"",
            "}"
        );
        verifyOut("X is 11");
    }

    @Test
    public void shouldContinueLoop() throws Exception {
        run(
            "main = {",
            "    var x = 0",
            "    var last = 0",
            "    while x < 8",
            "        x += 1",
            "        continue, if x % 2 == 0",
            "        say \"x is #{x}\"",
            "    end",
            "    say 'got it!'",
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
    public void shouldCompileNestedLoop() throws Exception {
        run(
            "main = {",
            "    var x = 0",
            "    var total = 0",
            "    while x < 10",
            "        var y = 0",
            "        while y < 10",
            "            y += 1",
            "            total += 1",
            "        end",
            "        x += 1",
            "    end",
            "    say \"Total iterations = #{total}\"",
            "}"
        );
        verifyOut("Total iterations = 100");
    }

    @Test
    public void shouldBeAbleToBreakLoopFromWithinEmbrace() throws Exception {
        run(
            "main = {",
            "    var counter = 0",
            "    while counter < 10",
            "        begin",
            "            counter += 1",
            "            hurl 'oops', if counter > 8",
            "        embrace error ->",
            "            break",
            "        end",
            "    end",
            "    say \"counter = #{counter}\"",
            "}"
        );
        verifyOut("counter = 9");
    }

    private void run(String... inputs) throws Exception {
        ((Invokable) compiler.compile(translate(inputs)).loadClass("test.Main").newInstance()).invoke();
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
}
