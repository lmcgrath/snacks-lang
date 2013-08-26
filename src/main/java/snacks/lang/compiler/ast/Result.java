package snacks.lang.compiler.ast;

import java.util.Objects;

public class Result implements AstNode {

    private final AstNode value;

    public Result(AstNode value) {
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitResult(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Result && Objects.equals(value, ((Result) o).value);
    }

    @Override
    public Type getType() {
        return value.getType();
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
        return "(result " + value + ")";
    }
}
