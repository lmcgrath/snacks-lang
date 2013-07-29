package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;

public class SubImport extends SyntaxNode {

    private final String identifier;
    private final IdentifierAlias alias;

    public SubImport(String identifier, IdentifierAlias alias) {
        this.identifier = identifier;
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof SubImport) {
            SubImport other = (SubImport) o;
            return Objects.equals(identifier, other.identifier)
                && Objects.equals(alias, other.alias);
        } else {
            return false;
        }
    }

    public String getAlias() {
        return alias.getAlias();
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, alias);
    }

    @Override
    public String toString() {
        return stringify(this, identifier, alias);
    }
}
