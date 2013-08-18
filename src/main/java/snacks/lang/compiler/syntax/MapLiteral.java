package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class MapLiteral extends Symbol implements Visitable {

    private final List<Symbol> entries;

    public MapLiteral(Symbol... entries) {
        this.entries = asList(entries);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitMapLiteral(this, state);
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