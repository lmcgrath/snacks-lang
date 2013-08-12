package iddic.lang.compiler;

public interface SourceStream extends AutoCloseable {

    public static final int EOF = -1;

    @Override
    void close();

    void consume();

    int lookAhead(int offset);

    int peek();

    Position position();
}
