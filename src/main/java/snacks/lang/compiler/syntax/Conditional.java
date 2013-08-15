package snacks.lang.compiler.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class Conditional extends Symbol {

    private final List<Symbol> cases;

    public Conditional(List<Symbol> cases) {
        this.cases = cases;
    }

    @Override
    public String toString() {
        return "(" + join(cases, ", ") + ")";
    }
}
