package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class IndexAssign extends SyntaxNode {

    private final ArgumentsList arguments;
    private final SyntaxNode expression;

    public IndexAssign(ArgumentsList arguments, SyntaxNode expression) {
        this.arguments = arguments;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitIndexAssign(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof IndexAssign) {
            IndexAssign other = (IndexAssign) o;
            return Objects.equals(arguments, other.arguments)
                && Objects.equals(expression, other.expression);
        } else {
            return false;
        }
    }

    public ArgumentsList getArguments() {
        return arguments;
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments, expression);
    }

    @Override
    public String toString() {
        return stringify(this, arguments, expression);
    }
}
