package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class NothingLiteral extends Symbol {

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof NothingLiteral;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "Nothing";
    }
}
