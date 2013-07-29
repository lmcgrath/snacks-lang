package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class ListLiteral extends SyntaxNode {

    private final List<SyntaxNode> elements;

    public ListLiteral(SyntaxNode... elements) {
        this.elements = asList(elements);
    }

    public ListLiteral(Collection<SyntaxNode> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitListLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ListLiteral && Objects.equals(elements, ((ListLiteral) o).elements);
    }

    public List<SyntaxNode> getElements() {
        return new ArrayList<>(elements);
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
