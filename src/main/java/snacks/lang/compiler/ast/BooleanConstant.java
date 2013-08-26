package snacks.lang.compiler.ast;

import static snacks.lang.compiler.Type.DOUBLE_TYPE;

import java.util.Objects;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class BooleanConstant implements AstNode {

    private final boolean value;

    public BooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) throws SnacksException {
        visitor.visitBooleanConstant(this);
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
