package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class IndexAccess extends SyntaxNode {

    private final ArgumentsList arguments;

    public IndexAccess(ArgumentsList arguments) {
        this.arguments = arguments;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitIndexAccess(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IndexAccess && Objects.equals(arguments, ((IndexAccess) o).arguments);
    }

    public ArgumentsList getArguments() {
        return arguments;
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
