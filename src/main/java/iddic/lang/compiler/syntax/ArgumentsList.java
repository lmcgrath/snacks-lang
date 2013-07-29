package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class ArgumentsList extends SyntaxNode {

    private final List<SyntaxNode> arguments;

    public ArgumentsList(SyntaxNode... arguments) {
        this.arguments = asList(arguments);
    }

    public ArgumentsList(Collection<SyntaxNode> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitArgumentsList(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ArgumentsList && Objects.equals(arguments, ((ArgumentsList) o).arguments);
    }

    public List<SyntaxNode> getArguments() {
        return new ArrayList<>(arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments);
    }

    @Override
    public String toString() {
        return stringify(this, arguments);
    }
}
