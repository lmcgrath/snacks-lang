package iddic.lang.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

public class StringLiteral implements Expression {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return '"' + escapeJava(value) + '"';
    }
}
