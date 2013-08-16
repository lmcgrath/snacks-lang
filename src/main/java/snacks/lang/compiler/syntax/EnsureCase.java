package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class EnsureCase extends Symbol {

    private final Symbol expression;

    public EnsureCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof EnsureCase && Objects.equals(expression, ((EnsureCase) o).expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return "(ensure " + expression + ")";
    }
}
