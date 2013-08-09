package iddic.lang.compiler;

public interface InputReader extends AutoCloseable {

    public static final int EOF = -1;

    @Override
    void close();

    void consume();

    void consumeLine();

    Segment getSegment(Position start);

    Segment getSegment(Position start, Position end);

    int lookAhead(int offset);

    int peek();

    Position position();

    int size();
}
