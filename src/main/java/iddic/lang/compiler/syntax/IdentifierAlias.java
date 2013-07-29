package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class IdentifierAlias extends SyntaxNode {

    private final String alias;

    public IdentifierAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitIdentifierAlias(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IdentifierAlias && Objects.equals(alias, ((IdentifierAlias) o).alias);
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }

    @Override
    public String toString() {
        return stringify(this, alias);
    }
}
