package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class Module extends Symbol implements Visitable {

    private final List<Symbol> elements;

    public Module(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitModule(this, state);
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
