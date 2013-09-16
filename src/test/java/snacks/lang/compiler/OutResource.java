package snacks.lang.compiler;

import static org.mockito.Mockito.spy;

import java.io.PrintStream;
import org.junit.rules.ExternalResource;

public final class OutResource extends ExternalResource {

    private final PrintStream mockOut;
    private final PrintStream systemOut;

    public OutResource() {
        this.mockOut = spy(System.out);
        this.systemOut = System.out;
    }

    public PrintStream getStream() {
        return mockOut;
    }

    public void mockSystemOut() {
        System.setOut(mockOut);
    }

    public void restoreSystemOut() {
        System.setOut(systemOut);
    }

    @Override
    protected void after() {
        restoreSystemOut();
    }

    @Override
    protected void before() throws Throwable {
        mockSystemOut();
    }
}
