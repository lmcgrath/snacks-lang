package iddic.lang.compiler.lexer;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

public class Token {

    private final Terminal kind;
    private final Object value;

    public Token(Terminal kind, Object value) {
        this.kind = kind;
        this.value = value;
    }

    public Terminal getKind() {
        return kind;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + kind + " \"" + escapeJava(value.toString()) + "\")";
    }
}
