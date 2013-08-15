package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class MapLiteral extends Symbol {

    private final List<Symbol> entries;

    public MapLiteral(Symbol... entries) {
        this.entries = asList(entries);
    }

    @Override
    public String toString() {
        return "map(" + join(entries, ", ") + ")";
    }
}
