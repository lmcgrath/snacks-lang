package snacks.lang.compiler.syntax;

import java.util.List;
import java.util.Set;
import beaver.Symbol;

public class RegexLiteral extends Symbol {

    private final List<Symbol> elements;
    private final Set<Character> options;

    public RegexLiteral(List<Symbol> elements, Set<Character> options) {
        this.elements = elements;
        this.options = options;
    }

    @Override
    public String toString() {
        return "r/" + elements + "/" + options;
    }
}
