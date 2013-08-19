package snacks.lang.compiler.ast;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.compiler.TypeOperator.STRING_TYPE;

import java.util.Objects;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

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
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitStringConstant(this, state);
    }

    @Override
    public Reference getReference() {
        throw new IllegalStateException();
    }

    @Override
    public Type getType() {
        return STRING_TYPE;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isReference() {
        return false;
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
