package iddic.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

public class IddicString implements Expression {

    private final String value;

    public IddicString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return '"' + escapeJava(value) + '"';
    }
}
