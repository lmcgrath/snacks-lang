package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class Result extends Symbol {

    private final Symbol expression;

    public Result(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Result && Objects.equals(expression, ((Result) o).expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return "(return " + expression + ")";
    }
}
