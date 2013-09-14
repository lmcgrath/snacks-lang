package snacks.lang.parser.syntax;

import java.util.Objects;

public class DoubleLiteral extends VisitableSymbol {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitDoubleLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DoubleLiteral && value == ((DoubleLiteral) o).value;
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
