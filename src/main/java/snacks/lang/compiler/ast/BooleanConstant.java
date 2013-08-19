package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.DOUBLE_TYPE;

import java.util.Objects;
import snacks.lang.SnacksException;

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
    public boolean equals(Object o) {
        return o == this || o instanceof BooleanConstant && value == ((BooleanConstant) o).value;
    }

    @Override
    public Type getType() {
        return DOUBLE_TYPE;
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
