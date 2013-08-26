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
    public void accept(AstVisitor visitor) {
        visitor.visitStringConstant(this);
    }

    @Override
    public Type getType() {
        return STRING_TYPE;
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
