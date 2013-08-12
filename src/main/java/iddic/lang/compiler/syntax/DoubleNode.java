package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;
import iddic.lang.compiler.lexer.Position;
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
    public Position getEnd() {
        return token.getEnd();
    }

    @Override
    public Position getStart() {
        return token.getStart();
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
