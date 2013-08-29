package snacks.lang.compiler.ast;

import java.util.Objects;

public class ExpressionConstant implements AstNode {

    private final AstNode value;

    public ExpressionConstant(AstNode value) {
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitExpressionConstant(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ExpressionConstant && Objects.equals(value, ((ExpressionConstant) o).value);
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public boolean isInvokable() {
        return false;
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "(constant " + value + ")";
    }
}
