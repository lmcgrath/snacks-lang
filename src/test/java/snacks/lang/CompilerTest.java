package snacks.lang;

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

    private void run(String... inputs) throws Exception {
        ((Invokable) compiler.compile(translate(inputs)).loadClass("test.Main").newInstance()).invoke();
    }

    private void verifyOut(String line) {
        verify(out.getStream()).println(line);
    }
}
