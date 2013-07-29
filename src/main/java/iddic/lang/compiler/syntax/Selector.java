package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class Selector extends SyntaxNode {

    private final SyntaxNode expression;
    private final SyntaxNode selector;

    public Selector(SyntaxNode expression, SyntaxNode selector) {
        this.expression = expression;
        this.selector = selector;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitSelector(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Selector) {
            Selector other = (Selector) o;
            return Objects.equals(expression, other.expression)
                && Objects.equals(selector, other.selector);
        } else {
            return false;
        }
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    public SyntaxNode getSelector() {
        return selector;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, selector);
    }

    @Override
    public String toString() {
        return stringify(this, expression, selector);
    }
}
