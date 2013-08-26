package snacks.lang.compiler.ast;

import static snacks.lang.compiler.Type.DOUBLE_TYPE;

import java.util.Objects;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class DoubleConstant implements AstNode {

    private final double value;

    public DoubleConstant(double value) {
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) throws SnacksException {
        visitor.visitDoubleConstant(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DoubleConstant && value == ((DoubleConstant) o).value;
    }

    @Override
    public Type getType() {
        return DOUBLE_TYPE;
    }

    public double getValue() {
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
