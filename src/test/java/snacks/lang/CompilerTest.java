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
        ClassLoader loader = compiler.compile(translate("main = () -> say 'Hello World!'"));
        ((Runnable) loader.loadClass("Snacks").newInstance()).run();
        verifyOut("Hello World!");
    }

    @Test
    public void shouldSayReference() throws Exception {
        ClassLoader loader = compiler.compile(translate(
            "speak = () -> say 'Woof'",
            "main = () -> speak()"
        ));
        ((Runnable) loader.loadClass("Snacks").newInstance()).run();
        verifyOut("Woof");
    }

    private void verifyOut(String line) {
        verify(out.getStream()).println(line);
    }
}
