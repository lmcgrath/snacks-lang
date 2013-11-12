package snacks.lang.runtime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static snacks.lang.parser.CompilerUtil.translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import snacks.lang.Invokable;
import snacks.lang.SnackDefinition;
import snacks.lang.compiler.*;
import snacks.lang.compiler.Compiler;
import snacks.lang.parser.SymbolEnvironment;

public class AbstractRuntimeTest {

    @Rule
    public final OutResource out;
    private Compiler compiler;
    private SnacksClassLoader loader;
    private ClassLoader previousLoader;

    public AbstractRuntimeTest() {
        out = new OutResource();
    }

    @Before
    public void setUp() {
        previousLoader = Thread.currentThread().getContextClassLoader();
        loader = new SnacksClassLoader();
        compiler = new Compiler(loader);
        Thread.currentThread().setContextClassLoader(loader);
    }

    @After
    public void tearDown() {
        Thread.currentThread().setContextClassLoader(previousLoader);
    }

    public void run(String... inputs) {
        try {
            List<SnackDefinition> definitions = compiler.compile(translate(new SymbolEnvironment(loader), inputs));
            for (SnackDefinition definition : definitions) {
                writeClass(new File(definition.getJavaName().replace('.', '/') + ".class"), definition.getBytes());
            }
            loader.defineSnacks(definitions);
            ((Invokable) loader.loadClass("test.main").newInstance()).invoke();
        } catch (ReflectiveOperationException exception) {
            throw new CompileException(exception);
        }
    }

    public void verifyOut(int value) {
        verify(out.getStream()).println(value);
    }

    public void verifyOut(String line) {
        verify(out.getStream()).println(line);
    }

    public void verifyNever(String line) {
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
