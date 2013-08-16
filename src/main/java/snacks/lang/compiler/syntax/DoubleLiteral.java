package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class DoubleLiteral extends Symbol {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DoubleLiteral && value == ((DoubleLiteral) o).value;
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
