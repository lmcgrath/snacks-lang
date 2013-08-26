package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.DOUBLE_TYPE;

import java.util.Objects;

public class BooleanConstant implements AstNode {

    private final boolean value;

    public BooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) {
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
