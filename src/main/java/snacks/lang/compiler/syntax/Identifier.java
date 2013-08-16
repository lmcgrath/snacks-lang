package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class Identifier extends Symbol {

    private final String value;

    public Identifier(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Identifier && Objects.equals(value, ((Identifier) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
