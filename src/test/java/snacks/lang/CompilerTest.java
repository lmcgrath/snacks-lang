package snacks.lang;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static snacks.lang.compiler.CompilerUtil.translate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CompilerTest {

    @Rule
    public final OutResource out;
    private Compiler compiler;

    public CompilerTest() {
        out = new OutResource();
    }

    @Before
    public void setUp() {
        compiler = new Compiler();
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
        verifyOut("6");
    }

    @Test
    public void shouldSayConstantReference() throws Exception {
        run(
            "bananas = 2 + 2",
            "main = () -> say bananas"
        );
        verifyOut("4");
    }

    @Test
    public void shouldSayMultiplyFunction() throws Exception {
        run(
            "multiply = (x) -> x * 2",
            "main = () -> say $ multiply 4"
        );
        verifyOut("8");
    }

    @Test
    public void shouldSayMultiplyWithTwoArguments() throws Exception {
        run(
            "multiply = (x y) -> x * y",
            "main = () -> say $ multiply 3 5"
        );
        verifyOut("15");
    }

    @Test
    public void shouldSayTripleWithThreeArguments() throws Exception {
        run(
            "triple = (x y z) -> x * y * z",
            "main = () -> say $ triple 9 3 348"
        );
        verifyOut("9396");
    }

    @Test
    public void shouldSayQuadrupleWithFourArguments() throws Exception {
        run(
            "quadruple = (w x y z) -> w * x * y * z",
            "main = () -> say $ quadruple 3 3 3 3"
        );
        verifyOut("81");
    }

    @Test
    public void shouldCompileBlock() throws Exception {
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
    public void shouldReferenceBlock() throws Exception {
        run(
            "waffles = {",
            "    say $ 2 + 3",
            "    say $ 4 * 5",
            "}",
            "main = () -> waffles()"
        );
        verifyOut("5");
        verifyOut("20");
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
        verifyOut("36");
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
        verifyOut("36");
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
        verifyOut("36");
        verifyNever("can't touch this");
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
        verifyOut("155");
    }

    @Test
    public void shouldPassFunctionIntoFunction() throws Exception {
        run(
            "operate = (op) -> op 2 4",
            "main = {",
            "    say $ operate `+`",
            "    say $ operate `*`",
            "}"
        );
        verifyOut("6");
        verifyOut("8");
    }

    private void run(String... inputs) throws Exception {
        ((Invokable) compiler.compile(translate(inputs)).loadClass("test.Main").newInstance()).invoke();
    }

    private void verifyOut(String line) {
        verify(out.getStream()).println(line);
    }

    private void verifyNever(String line) {
        verify(out.getStream(), never()).println(line);
    }
}
