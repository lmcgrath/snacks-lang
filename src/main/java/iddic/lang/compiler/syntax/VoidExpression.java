package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

public final class VoidExpression extends SyntaxNode {

    public static final VoidExpression INSTANCE = new VoidExpression();

    private VoidExpression() {
        // intentionally empty
    }

    @Override
    public String toString() {
        return stringify(this);
    }
}
