package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.util.Objects;
import beaver.Symbol;

public class CharacterLiteral extends Symbol {

    private final char value;

    public CharacterLiteral(char value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof CharacterLiteral && value == ((CharacterLiteral) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "'" + escapeJava(String.valueOf(value)) + "'";
    }
}
