package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import beaver.Symbol;

public class StringLiteral extends Symbol {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return '"' + escapeJava(value) + '"';
    }
}
