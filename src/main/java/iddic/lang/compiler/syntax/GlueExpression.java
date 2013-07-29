package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;
import iddic.lang.IddicException;

public class GlueExpression extends SyntaxNode {

    private final String className;

    public GlueExpression(String className) {
        this.className = className;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitGlueExpression(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof GlueExpression && Objects.equals(className, ((GlueExpression) o).className);
    }

    public String getClassName() {
        return className;
    }

    @Override
    public int hashCode() {
        return Objects.hash(className);
    }

    @Override
    public String toString() {
        return stringify(this, className);
    }
}
