package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class Declaration extends SyntaxNode {

    private final String identifier;
    private final SyntaxNode expression;
    private final List<MetaAnnotation> meta;

    public Declaration(Declaration declaration, MetaAnnotation... meta) {
        super(declaration.getSource());
        this.identifier = declaration.identifier;
        this.expression = declaration.expression;
        this.meta = asList(meta);
    }

    public Declaration(Declaration declaration, Collection<MetaAnnotation> meta) {
        super(declaration.getSource());
        this.identifier = declaration.identifier;
        this.expression = declaration.expression;
        this.meta = new ArrayList<>(meta);
    }

    public Declaration(String identifier, SyntaxNode expression) {
        this.identifier = identifier;
        this.expression = expression;
        this.meta = emptyList();
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitDeclaration(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Declaration) {
            Declaration other = (Declaration) o;
            return Objects.equals(identifier, other.identifier)
                && Objects.equals(expression, other.expression)
                && Objects.equals(meta, other.meta);
        } else {
            return false;
        }
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<MetaAnnotation> getMeta() {
        return new ArrayList<>(meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, expression, meta);
    }

    @Override
    public String toString() {
        return stringify(this, identifier, expression, meta);
    }
}
