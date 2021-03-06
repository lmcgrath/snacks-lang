package snacks.lang.ast;

import static snacks.lang.Types.booleanType;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class ReferencesEqual extends AstNode {

    private final AstNode left;
    private final AstNode right;

    public ReferencesEqual(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
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

    @Override
    public void generate(Generator generator) {
        generator.generateReferencesEqual(this);
    }

    public AstNode getLeft() {
        return left;
    }

    public AstNode getRight() {
        return right;
    }

    @Override
    public Type getType() {
        return booleanType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printReferencesEqual(this);
    }

    @Override
    public String toString() {
        return "(ReferencesEquals " + left + " " + right + ")";
    }
}
