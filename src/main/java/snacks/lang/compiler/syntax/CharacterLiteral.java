package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import beaver.Symbol;

public class CharacterLiteral extends Symbol {

    private final char value;

    public CharacterLiteral(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "'" + escapeJava(String.valueOf(value)) + "'";
    }
}
