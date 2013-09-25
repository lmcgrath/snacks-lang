package snacks.lang.ast;

import static snacks.lang.Type.BOOLEAN_TYPE;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class LogicalOr extends AstNode {

    private final AstNode left;
    private final AstNode right;

    public LogicalOr(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof LogicalOr) {
            LogicalOr other = (LogicalOr) o;
            return new EqualsBuilder()
                .append(left, other.left)
                .append(right, other.right)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateLogicalOr(this);
    }

    public AstNode getLeft() {
        return left;
    }

    public AstNode getRight() {
        return right;
    }

    @Override
    public Type getType() {
        return BOOLEAN_TYPE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "(" + left + " || " + right + ")";
    }
}
