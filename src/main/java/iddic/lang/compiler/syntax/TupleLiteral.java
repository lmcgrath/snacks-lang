package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class TupleLiteral extends SyntaxNode {

    private final List<SyntaxNode> elements;

    public TupleLiteral(SyntaxNode... elements) {
        this.elements = asList(elements);
    }

    public TupleLiteral(Collection<SyntaxNode> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitTupleLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TupleLiteral && Objects.equals(elements, ((TupleLiteral) o).elements);
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
