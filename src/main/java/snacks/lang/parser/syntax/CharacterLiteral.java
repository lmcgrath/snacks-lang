package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.util.Objects;

public class CharacterLiteral extends VisitableSymbol {

    private final char value;

    public CharacterLiteral(char value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
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
