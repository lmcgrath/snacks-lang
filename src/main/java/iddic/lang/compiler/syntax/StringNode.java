package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;
import iddic.lang.compiler.lexer.Token;

public class StringNode implements SyntaxNode {

    private final Token token;

    public StringNode(Token token) {
        this.token = token;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitStringNode(this, state);
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
