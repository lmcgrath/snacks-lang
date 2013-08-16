package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class DefaultCase extends Symbol {

    private final Symbol expression;

    public DefaultCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DefaultCase && Objects.equals(expression, ((DefaultCase) o).expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return "(else " + expression + ")";
    }
}
