package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class MetaAnnotation extends SyntaxNode {

    private final String identifier;
    private final SyntaxNode value;

    public MetaAnnotation(String identifier, SyntaxNode value) {
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitMetaAnnotation(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MetaAnnotation) {
            MetaAnnotation other = (MetaAnnotation) o;
            return Objects.equals(identifier, other.identifier)
                && Objects.equals(value, other.value);
        } else {
            return false;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public SyntaxNode getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, value);
    }

    @Override
    public String toString() {
        return stringify(this, identifier, value);
    }
}
