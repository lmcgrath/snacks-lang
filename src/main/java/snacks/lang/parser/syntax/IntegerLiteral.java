package snacks.lang.parser.syntax;

import java.util.Objects;

public class IntegerLiteral extends VisitableSymbol {

    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitIntegerLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IntegerLiteral && value == ((IntegerLiteral) o).value;
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
