package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;

public class ReturnExpression extends SyntaxNode {

    private final SyntaxNode result;

    public ReturnExpression(SyntaxNode result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ReturnExpression && Objects.equals(result, ((ReturnExpression) o).result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }

    @Override
    public String toString() {
        return stringify(this, result);
    }
}
