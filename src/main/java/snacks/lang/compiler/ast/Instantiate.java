package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class Instantiate implements AstNode {

    private final AstNode invokable;

    public Instantiate(AstNode invokable) {
        this.invokable = invokable;
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitInvoke(this, state);
    }

    public AstNode getInvokable() {
        return invokable;
    }

    @Override
    public Type getType() {
        return invokable.getType().getParameters().get(1);
    }
}
