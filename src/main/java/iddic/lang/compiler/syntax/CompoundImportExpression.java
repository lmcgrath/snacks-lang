package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class CompoundImportExpression extends SyntaxNode {

    private final QualifiedIdentifier identifier;
    private final List<SubImport> subImports;

    public CompoundImportExpression(QualifiedIdentifier identifier, SubImport... subImports) {
        this.identifier = identifier;
        this.subImports = asList(subImports);
    }

    public CompoundImportExpression(QualifiedIdentifier identifier, Collection<SubImport> subImports) {
        this.identifier = identifier;
        this.subImports = new ArrayList<>(subImports);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitCompoundImportExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof CompoundImportExpression) {
            CompoundImportExpression other = (CompoundImportExpression) o;
            return Objects.equals(identifier, other.identifier)
                && Objects.equals(subImports, other.subImports);
        } else {
            return false;
        }
    }

    public QualifiedIdentifier getIdentifier() {
        return identifier;
    }

    public List<SubImport> getSubImports() {
        return new ArrayList<>(subImports);
    }

    @Override
    public String toString() {
        return stringify(this, identifier, subImports);
    }
}
