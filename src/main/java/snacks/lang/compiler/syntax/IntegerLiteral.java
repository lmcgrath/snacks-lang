package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class IntegerLiteral extends Symbol {

    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IntegerLiteral && value == ((IntegerLiteral) o).value;
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
