package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;
import iddic.lang.compiler.lexer.Token;

public class BooleanNode implements SyntaxNode {

    private final Token token;

    public BooleanNode(Token token) {
        this.token = token;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitBooleanNode(this, state);
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
