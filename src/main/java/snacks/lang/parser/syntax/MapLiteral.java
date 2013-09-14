package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class MapLiteral extends VisitableSymbol {

    private final List<Symbol> entries;

    public MapLiteral(Symbol... entries) {
        this.entries = asList(entries);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitMapLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof MapLiteral && Objects.equals(entries, ((MapLiteral) o).entries);
    }

    public List<Symbol> getEntries() {
        return entries;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }

    @Override
    public String toString() {
        return "map(" + join(entries, ", ") + ")";
    }
}
