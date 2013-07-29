package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class MapLiteral extends SyntaxNode {

    private final List<MapElement> elements;

    public MapLiteral(MapElement... elements) {
        this.elements = asList(elements);
    }

    public MapLiteral(Collection<MapElement> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitMapLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof MapLiteral && Objects.equals(elements, ((MapLiteral) o).elements);
    }

    public List<MapElement> getElements() {
        return elements;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return stringify(this, elements);
    }
}
