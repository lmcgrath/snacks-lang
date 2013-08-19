package snacks.lang.compiler.ast;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.compiler.ast.Type.STRING_TYPE;

import java.util.Objects;

public class StringConstant implements AstNode {

    private final String value;

    public StringConstant(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StringConstant && Objects.equals(value, ((StringConstant) o).value);
    }

    @Override
    public Type getType() {
        return STRING_TYPE;
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
