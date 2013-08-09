package iddic.lang.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

public class StreamReader implements InputReader {

    private static final int READ_CHUNK_SIZE = 1024;
    private static final int INITIAL_BUFFER_SIZE = 1024;

    private final Reader reader;
    public String source = "<unknown>";
    private char[] data;
    private int position;
    private int line;
    private int column;
    private int size;
    private boolean eof;

    public StreamReader(InputStream stream) {
        reader = new InputStreamReader(stream);
        data = new char[INITIAL_BUFFER_SIZE];
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException exception) {
            // intentionally empty
        }
    }

    @Override
    public void consume() {
        hasMore();
        if (eof) {
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
        while (!eof && peek() != '\n') {
            consume();
        }
        if (!eof) {
            consume();
        }
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
        hasMore();
        if (offset < 0) {
            offset++;
        }
        int adjustedOffset = position + offset - 1;
        return (adjustedOffset >= 0 && adjustedOffset < size) ? data[adjustedOffset] : EOF;
    }

    @Override
    public int peek() {
        hasMore();
        return eof ? EOF : data[position];
    }

    @Override
    public Position position() {
        return new Position(source, position, line, column);
    }

    @Override
    public int size() {
        return size;
    }

    private String getText(int start, int stop) {
        hasMore();
        if (eof) {
            return "";
        } else {
            int endPosition = stop - start;
            if (endPosition >= size) {
                endPosition = size - 1;
            }
            return start < size ? new String(data, start, endPosition) : "";
        }
    }

    private void hasMore() {
        if (!eof && position >= size) {
            int readChunkSize = READ_CHUNK_SIZE;
            if (size + readChunkSize > data.length) {
                data = Arrays.copyOf(data, data.length * 2);
            }
            try {
                int numRead = reader.read(data, position, readChunkSize);
                if (numRead == -1) {
                    eof = true;
                } else {
                    size += numRead;
                }
            } catch (IOException exception) {
                eof = true;
            }
        }
    }
}
