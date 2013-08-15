package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;

import java.util.List;
import beaver.Symbol;

public class StringInterpolation extends Symbol {

    private final List<Symbol> elements;

    public StringInterpolation(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public String toString() {
        return "(Interpolation " + elements + ")";
    }
}
