package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class ImportExpression extends SyntaxNode {

    private final QualifiedIdentifier identifier;
    private final IdentifierAlias alias;

    public ImportExpression(QualifiedIdentifier identifier, IdentifierAlias alias) {
        this.identifier = identifier;
        this.alias = alias;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitImportExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ImportExpression) {
            ImportExpression other = (ImportExpression) o;
            return Objects.equals(identifier, other.identifier)
                && Objects.equals(alias, other.alias);
        } else {
            return false;
        }
    }

    public IdentifierAlias getAlias() {
        return alias;
    }

    public QualifiedIdentifier getIdentifier() {
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
