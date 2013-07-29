package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class DoubleLiteral extends SyntaxNode {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitDoubleLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DoubleLiteral && value == ((DoubleLiteral) o).value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return stringify(this, value);
    }
}
