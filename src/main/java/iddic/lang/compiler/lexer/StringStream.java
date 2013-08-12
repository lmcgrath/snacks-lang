package iddic.lang.compiler.lexer;

public class StringStream implements TextStream {

    private final String data;
    private final int size;
    public String source = "<unknown>";
    private int position = 0;
    private int line;
    private int column;

    public StringStream(String input) {
        this.data = input;
        this.size = input.length();
    }

    @Override
    public void close() {
        // intentionally empty
    }

    @Override
    public void consume() {
        if (position >= size) {
            assert lookAhead(1) == EOF;
            throw new IllegalStateException("cannot consume EOF");
        } else if (position < size) {
            if (peek() == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }
            position++;
        }
    }

    @Override
    public void consumeLine() {
        while (peek() != EOF && peek() != '\n') {
            consume();
        }
        if (peek() != EOF) {
            consume();
        }
    }

    @Override
    public Segment getSegment(Position start) {
        return getSegment(start, position());
    }

    @Override
    public int lookAhead(int offset) {
        if (offset < 0) {
            offset++;
        }
        int adjustedOffset = position + offset - 1;
        return (adjustedOffset >= 0 && adjustedOffset < size) ? data.charAt(adjustedOffset) : EOF;
    }

    @Override
    public int peek() {
        return (position < size) ? data.charAt(position) : EOF;
    }

    @Override
    public Position position() {
        return new Position(source, position, line, column);
    }

    @Override
    public String toString() {
        return data;
    }

    private Segment getSegment(Position start, Position end) {
        return new Segment(getText(start.getOffset(), end.getOffset()), start, end);
    }

    private String getText(int start, int stop) {
        if (stop >= size) {
            stop = size - 1;
        }
        return start < size ? data.substring(start, stop) : "";
    }
}
