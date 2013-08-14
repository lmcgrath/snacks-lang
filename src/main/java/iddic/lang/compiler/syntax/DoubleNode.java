package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;
import iddic.lang.compiler.lexer.Token;

public class DoubleNode implements SyntaxNode {

    private final Token token;

    public DoubleNode(Token token) {
        this.token = token;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitDoubleNode(this, state);
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
