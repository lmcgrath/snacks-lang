package snacks.lang.compiler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static snacks.lang.compiler.CompilerUtil.translate;

import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksException;

public class CompilerTest {

    private Compiler compiler;

    @Before
    public void setUp() {
        compiler = new Compiler();
    }

    @Test
    public void shouldSayHello() throws SnacksException {
        PrintStream systemOut = System.out;
        PrintStream out = mock(PrintStream.class);
        try {
            System.setOut(out);
            compiler.compile(translate("main = () -> say 'Hello World!'")).run();
            verify(out).println("Hello World!");
        } finally {
            System.setOut(systemOut);
        }
    }
}
