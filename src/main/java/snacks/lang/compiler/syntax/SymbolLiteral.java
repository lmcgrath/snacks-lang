package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class SymbolLiteral extends Symbol {

    private final String value;

    public SymbolLiteral(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof SymbolLiteral && Objects.equals(value, ((SymbolLiteral) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ":" + value;
    }
}
