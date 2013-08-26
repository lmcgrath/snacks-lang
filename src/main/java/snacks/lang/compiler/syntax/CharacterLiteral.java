package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class CharacterLiteral extends Symbol implements Visitable {

    private final char value;

    public CharacterLiteral(char value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) throws SnacksException {
        visitor.visitCharacterLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof CharacterLiteral && value == ((CharacterLiteral) o).value;
    }

    public char getValue() {
        return value;
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
