package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class ListLiteral extends Symbol implements Visitable {

    private final List<Symbol> elements;

    public ListLiteral(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitListLiteral(this, state);
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