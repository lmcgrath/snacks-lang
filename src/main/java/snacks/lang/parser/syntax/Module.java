package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class Module extends VisitableSymbol {

    private final List<Symbol> elements;

    public Module(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitModule(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Module && Objects.equals(elements, ((Module) o).elements);
    }

    public List<Symbol> getElements() {
        return elements;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return "(" + join(elements, "; ") + ")";
    }
}
