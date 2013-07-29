package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class Access extends SyntaxNode {

    private final String value;

    public Access(String value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitAccess(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Access && Objects.equals(value, ((Access) o).value);
    }

    public String getValue() {
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
