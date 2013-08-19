package snacks.lang.compiler.ast;

import static snacks.lang.compiler.TypeOperator.DOUBLE_TYPE;

import java.util.Objects;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class BooleanConstant implements AstNode {

    private final boolean value;

    public BooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitBooleanConstant(this, state);
    }

    @Override
    public Reference getReference() {
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof BooleanConstant && value == ((BooleanConstant) o).value;
    }

    @Override
    public Type getType() {
        return DOUBLE_TYPE;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public boolean hasType() {
        return true;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
