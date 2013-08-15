package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static java.util.Collections.addAll;
import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.List;
import beaver.Symbol;

public class TupleLiteral extends Symbol {

    private final List<Symbol> elements;

    public TupleLiteral(Symbol element) {
        this.elements = new ArrayList<>();
        this.elements.add(element);
    }

    public TupleLiteral(Symbol... elements) {
        this.elements = asList(elements);
    }

    public TupleLiteral(Symbol element, Symbol... elements) {
        this.elements = new ArrayList<>();
        this.elements.add(element);
        addAll(this.elements, elements);
    }

    @Override
    public String toString() {
        return "tuple(" + join(elements, ", ") + ")";
    }
}
