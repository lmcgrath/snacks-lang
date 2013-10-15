package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class AndExpression extends VisitableSymbol {

    private final Symbol left;
    private final Symbol right;

    public AndExpression(Symbol left, Symbol right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAndExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AndExpression) {
            AndExpression other = (AndExpression) o;
            return new EqualsBuilder()
                .append(left, other.left)
                .append(right, other.right)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getLeft() {
        return left;
    }

    public Symbol getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return "(AndExpression " + left + " " + right + ")";
    }
}
