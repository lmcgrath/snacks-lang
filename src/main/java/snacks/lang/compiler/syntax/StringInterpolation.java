package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class StringInterpolation extends Symbol implements Visitable {

    private final List<Symbol> elements;

    public StringInterpolation(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitStringInterpolation(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StringInterpolation && Objects.equals(elements, ((StringInterpolation) o).elements);
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
        return "(Interpolation " + elements + ")";
    }
}
