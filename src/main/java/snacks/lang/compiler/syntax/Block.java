package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class Block extends Symbol {

    private final List<Symbol> elements;

    public Block(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public String toString() {
        return "{" + join(elements, "; ") + "}";
    }
}
