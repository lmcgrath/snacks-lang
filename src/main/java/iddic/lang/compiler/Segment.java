package iddic.lang.compiler;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Segment {

    private final String value;
    private final Position start;
    private final Position end;

    public Segment(String value, Position start, Position end) {
        this.value = value;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Segment) {
            Segment other = (Segment) o;
            return new EqualsBuilder()
                .append(value, other.value)
                .append(start, other.start)
                .append(end, other.end)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, start, end);
    }

    @Override
    public String toString() {
        return start.getSource() + " (" + start.getLine() + "," + start.getColumn() + "-" + end.getLine() + "," + end.getColumn() + ")";
    }
}
