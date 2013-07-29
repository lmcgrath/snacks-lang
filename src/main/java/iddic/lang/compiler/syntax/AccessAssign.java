package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class AccessAssign extends SyntaxNode {

    private final String value;
    private final SyntaxNode expression;

    public AccessAssign(String value, SyntaxNode expression) {
        this.value = value;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitAccessAssign(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AccessAssign) {
            AccessAssign other = (AccessAssign) o;
            return Objects.equals(value, other.value)
                && Objects.equals(expression, other.expression);
        } else {
            return false;
        }
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, expression);
    }

    @Override
    public String toString() {
        return stringify(this, value, expression);
    }
}
