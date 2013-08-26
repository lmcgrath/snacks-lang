package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class IntegerLiteral extends Symbol implements Visitable {

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
