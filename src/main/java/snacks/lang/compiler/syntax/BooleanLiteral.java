package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class BooleanLiteral extends Symbol {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof BooleanLiteral && value == ((BooleanLiteral) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value ? "True" : "False";
    }
}
