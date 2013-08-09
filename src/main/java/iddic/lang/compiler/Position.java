package iddic.lang.compiler;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Position {

    private final String source;
    private final int offset;
    private final int line;
    private final int column;

    public Position(String source, int offset, int line, int column) {
        this.source = source;
        this.offset = offset;
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Position) {
            Position other = (Position) o;
            return new EqualsBuilder()
                .append(source, other.source)
                .append(offset, other.offset)
                .append(line, other.line)
                .append(column, other.column)
                .isEquals();
        } else {
            return false;
        }
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }

    public int getOffset() {
        return offset;
    }

    public String getSource() {
        return source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, line, column);
    }

    @Override
    public String toString() {
        return source + " (" + line + "," + column + ")";
    }
}
