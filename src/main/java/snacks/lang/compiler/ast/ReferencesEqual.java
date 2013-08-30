package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.BOOLEAN_TYPE;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ReferencesEqual implements AstNode {

    private final AstNode left;
    private final AstNode right;

    public ReferencesEqual(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitReferencesEqual(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ReferencesEqual) {
            ReferencesEqual other = (ReferencesEqual) o;
            return new EqualsBuilder()
                .append(left, other.left)
                .append(right, other.right)
                .isEquals();
        } else {
            return false;
        }
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
    public boolean isInvokable() {
        return false;
    }
}
