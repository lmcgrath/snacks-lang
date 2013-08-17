package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class TypeSpec extends Symbol {

    private final Symbol type;

    public TypeSpec(Symbol type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeSpec && Objects.equals(type, ((TypeSpec) o).type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
