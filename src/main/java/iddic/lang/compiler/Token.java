package iddic.lang.compiler;

import static iddic.lang.compiler.Terminals.nameOf;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

public class Token {

    private final int kind;
    private final Segment segment;
    private final Object value;

    public Token(int kind, Segment segment, Object value) {
        this.kind = kind;
        this.segment = segment;
        this.value = value;
    }

    public Position getEnd() {
        return segment.getEnd();
    }

    public int getKind() {
        return kind;
    }

    public Position getStart() {
        return segment.getStart();
    }

    public String getText() {
        return segment.getValue();
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + nameOf(kind) + " \"" + escapeJava(segment.getValue()) + "\" " + getStart() + ")";
    }
}
