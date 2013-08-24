package snacks.lang.compiler.ast;

import static snacks.lang.compiler.Type.INTEGER_TYPE;

import java.util.Objects;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class IntegerConstant implements AstNode {

    private final int value;

    public IntegerConstant(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IntegerConstant && value == ((IntegerConstant) o).value;
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitIntegerConstant(this, state);
    }

    @Override
    public Type getType() {
        return INTEGER_TYPE;
    }

    public int getValue() {
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
