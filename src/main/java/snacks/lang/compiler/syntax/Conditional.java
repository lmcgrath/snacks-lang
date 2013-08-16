package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class Conditional extends Symbol {

    private final List<Symbol> cases;

    public Conditional(List<Symbol> cases) {
        this.cases = cases;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Conditional && Objects.equals(cases, ((Conditional) o).cases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cases);
    }

    @Override
    public String toString() {
        return "(" + join(cases, ", ") + ")";
    }
}
