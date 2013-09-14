package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;
import static java.util.Collections.addAll;
import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class SetLiteral extends VisitableSymbol {

    private final List<Symbol> elements;

    public SetLiteral(Symbol... elements) {
        this.elements = asList(elements);
    }

    public SetLiteral(Symbol element, Symbol... elements) {
        this.elements = new ArrayList<>();
        this.elements.add(element);
        addAll(this.elements, elements);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitSetLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof SetLiteral && Objects.equals(elements, ((SetLiteral) o).elements);
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
        return "set(" + join(elements, ", ") + ")";
    }
}
