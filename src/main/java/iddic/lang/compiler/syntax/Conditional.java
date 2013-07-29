package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class Conditional extends SyntaxNode {

    private final SyntaxNode head;
    private final List<SyntaxNode> tail;

    public Conditional(SyntaxNode head, SyntaxNode[] tail) {
        this.head = head;
        this.tail = asList(tail);
    }

    public Conditional(SyntaxNode head, Collection<SyntaxNode> tail) {
        this.head = head;
        this.tail = new ArrayList<>(tail);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitConditional(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Conditional) {
            Conditional other = (Conditional) o;
            return Objects.equals(head, other.head)
                && Objects.equals(tail, other.tail);
        } else {
            return false;
        }
    }

    public SyntaxNode getHead() {
        return head;
    }

    public List<SyntaxNode> getTail() {
        return new ArrayList<>(tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    @Override
    public String toString() {
        return stringify(this, head, tail);
    }
}
