package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class XRangeLiteral extends SyntaxNode {

    private final SyntaxNode begin;
    private final SyntaxNode end;

    public XRangeLiteral(SyntaxNode begin, SyntaxNode end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitXRangeLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof XRangeLiteral) {
            XRangeLiteral other = (XRangeLiteral) o;
            return Objects.equals(begin, other.begin)
                && Objects.equals(end, other.end);
        } else {
            return false;
        }
    }

    public SyntaxNode getRangeBegin() {
        return begin;
    }

    public SyntaxNode getRangeEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end);
    }

    @Override
    public String toString() {
        return stringify(this, begin, end);
    }
}
