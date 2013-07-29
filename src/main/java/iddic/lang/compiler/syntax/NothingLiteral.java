package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class NothingLiteral extends SyntaxNode {

    public NothingLiteral() {
        // intentionally empty
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitNothingLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof NothingLiteral;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return stringify(this);
    }
}
