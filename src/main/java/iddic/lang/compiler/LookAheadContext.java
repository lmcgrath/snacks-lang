package iddic.lang.compiler;

import org.antlr.v4.runtime.IntStream;

public class LookAheadContext implements AutoCloseable {

    private final IntStream input;
    private final int index;
    private final int mark;

    public LookAheadContext(IntStream input) {
        this.input = input;
        this.index = input.index();
        this.mark = input.mark();
    }

    @Override
    public void close() {
        if (index != input.index()) {
            input.seek(index);
        }
        input.release(mark);
    }
}
