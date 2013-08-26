package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class ListLiteral extends Symbol implements Visitable {

    private final List<Symbol> elements;

    public ListLiteral(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitListLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ListLiteral && Objects.equals(elements, ((ListLiteral) o).elements);
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
        return "list(" + join(elements, ", ") + ")";
    }
}
