package iddic.lang.compiler;

public class StringReader implements InputReader {

    private final char[] data;
    private final int size;
    public String source = "<unknown>";
    private int position = 0;
    private int line;
    private int column;

    public StringReader(String input) {
        this.data = input.toCharArray();
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
    public Position position() {
        return new Position(source, position, line, column);
    }

    @Override
    public Segment getSegment(Position start) {
        return getSegment(start, position());
    }

    @Override
    public Segment getSegment(Position start, Position end) {
        return new Segment(getText(start.getOffset(), end.getOffset()), start, end);
    }

    @Override
    public int lookAhead(int offset) {
        if (offset < 0) {
            offset++;
        }
        int adjustedOffset = position + offset - 1;
        return (adjustedOffset >= 0 && adjustedOffset < size) ? data[adjustedOffset] : EOF;
    }

    @Override
    public int peek() {
        return (position < size) ? data[position] : EOF;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return new String(data);
    }

    private String getText(int start, int stop) {
        int endPosition = stop - start;
        if (endPosition >= size) {
            endPosition = size - 1;
        }
        return start < size ? new String(data, start, endPosition) : "";
    }
}
