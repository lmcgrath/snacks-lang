package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class ModuleDeclaration extends SyntaxNode {

    private List<SyntaxNode> expressions;

    public ModuleDeclaration(Collection<SyntaxNode> expressions) {
        this.expressions = new ArrayList<>(expressions);
    }

    public ModuleDeclaration(SyntaxNode... expressions) {
        this.expressions = asList(expressions);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitModuleDeclaration(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ModuleDeclaration && Objects.equals(expressions, ((ModuleDeclaration) o).expressions);
    }

    public List<SyntaxNode> getExpressions() {
        return new ArrayList<>(expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressions);
    }

    @Override
    public String toString() {
        return stringify(this, expressions);
    }
}
