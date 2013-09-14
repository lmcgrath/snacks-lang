package snacks.lang.parser.syntax;

import java.util.Objects;

public class BooleanLiteral extends VisitableSymbol {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitBooleanLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof BooleanLiteral && value == ((BooleanLiteral) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value ? "True" : "False";
    }
}
