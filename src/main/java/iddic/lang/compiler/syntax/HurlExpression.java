package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;

public class HurlExpression extends SyntaxNode {

    private final SyntaxNode expression;

    public HurlExpression(SyntaxNode expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof HurlExpression && Objects.equals(expression, ((HurlExpression) o).expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return stringify(this, expression);
    }
}
