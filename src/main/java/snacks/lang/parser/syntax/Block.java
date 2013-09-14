package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class Block extends VisitableSymbol {

    private final List<Symbol> elements;

    public Block(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitBlock(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Block && Objects.equals(elements, ((Block) o).elements);
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
        return "{" + join(elements, "; ") + "}";
    }
}
