package iddic.lang.compiler;

public class CharStream {

    public static final int EOF = -1;

    private final char[] data;
    private final int size;
    private int position = 0;

    public CharStream(String input) {
        this.data = input.toCharArray();
        this.size = input.length();
    }

    public void consume() {
        if (position >= size) {
            assert lookAhead(1) == EOF;
            throw new IllegalStateException("cannot consume EOF");
        } else if (position < size) {
            position++;
        }
    }

    public String getText(int start) {
        return getText(start, position() - 1);
    }

    public String getText(int start, int stop) {
        int endPosition = stop - start + 1;
        if (endPosition >= size) {
            endPosition = size - 1;
        }
        return start < size ? new String(data, start, endPosition) : "";
    }

    public int lookAhead(int offset) {
        if (offset < 0) {
            offset++;
        }
        int adjustedOffset = position + offset - 1;
        return (adjustedOffset >= 0 && adjustedOffset < size) ? data[adjustedOffset] : EOF;
    }

    public int peek() {
        return (position < size) ? data[position] : EOF;
    }

    public int position() {
        return position;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return new String(data);
    }
}
