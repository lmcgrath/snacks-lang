package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class Embrace extends SyntaxNode {

    private final SyntaxNode begin;
    private final List<SyntaxNode> embrace;
    private final SyntaxNode ensure;

    public Embrace(SyntaxNode begin, SyntaxNode[] embrace, SyntaxNode ensure) {
        this.begin = begin;
        this.embrace = asList(embrace);
        this.ensure = ensure;
    }

    public Embrace(SyntaxNode begin, Collection<SyntaxNode> embrace, SyntaxNode ensure) {
        this.begin = begin;
        this.embrace = new ArrayList<>(embrace);
        this.ensure = ensure;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitEmbrace(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Embrace) {
            Embrace other = (Embrace) o;
            return Objects.equals(begin, other.begin)
                && Objects.equals(embrace, other.embrace)
                && Objects.equals(ensure, other.ensure);
        } else {
            return false;
        }
    }

    public SyntaxNode getBegin() {
        return begin;
    }

    public List<SyntaxNode> getEmbrace() {
        return new ArrayList<>(embrace);
    }

    public SyntaxNode getEnsure() {
        return ensure;
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, embrace, ensure);
    }

    @Override
    public String toString() {
        return stringify(this, begin, embrace, ensure);
    }
}
