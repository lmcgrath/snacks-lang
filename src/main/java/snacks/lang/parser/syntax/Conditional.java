package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class Conditional extends VisitableSymbol {

    private final List<Symbol> cases;

    public Conditional(List<Symbol> cases) {
        this.cases = cases;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitConditional(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Conditional && Objects.equals(cases, ((Conditional) o).cases);
    }

    public List<Symbol> getCases() {
        return cases;
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
