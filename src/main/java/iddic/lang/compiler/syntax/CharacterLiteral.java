package iddic.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class CharacterLiteral extends SyntaxNode {

    private final char value;

    public CharacterLiteral(char value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitCharacterLiteral(this, state);
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
        return stringify(this, "'" + escapeJava(String.valueOf(value)) + "'");
    }
}
