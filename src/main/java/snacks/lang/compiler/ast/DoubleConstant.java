package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.DOUBLE_TYPE;

import java.util.Objects;

public class DoubleConstant implements AstNode {

    private final double value;

    public DoubleConstant(double value) {
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) {
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

    @Override
    public boolean isInvokable() {
        return false;
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
