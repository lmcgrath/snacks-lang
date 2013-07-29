package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class SetLiteral extends SyntaxNode {

    private final List<SyntaxNode> elements;

    public SetLiteral(SyntaxNode... elements) {
        this.elements = asList(elements);
    }

    public SetLiteral(Collection<SyntaxNode> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitSetLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof SetLiteral && Objects.equals(elements, ((SetLiteral) o).elements);
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
