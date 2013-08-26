package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.util.Objects;
import beaver.Symbol;

public class StringLiteral extends Symbol implements Visitable {

    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitStringLiteral(this);
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
        return '"' + escapeJava(value) + '"';
    }
}
