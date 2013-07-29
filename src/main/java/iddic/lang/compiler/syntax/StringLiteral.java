package iddic.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class StringLiteral extends SyntaxNode {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitStringLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StringLiteral && Objects.equals(value, ((StringLiteral) o).value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return stringify(this, '"' + escapeJava(value) + '"');
    }
}
