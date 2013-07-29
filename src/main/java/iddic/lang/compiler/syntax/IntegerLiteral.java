package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class IntegerLiteral extends SyntaxNode {

    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitIntegerLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IntegerLiteral && value == ((IntegerLiteral) o).value;
    }

    public int getValue() {
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
