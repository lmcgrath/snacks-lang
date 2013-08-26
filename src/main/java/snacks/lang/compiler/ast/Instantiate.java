package snacks.lang.compiler.ast;

public class Instantiate implements AstNode {

    private final AstNode invokable;

    public Instantiate(AstNode invokable) {
        this.invokable = invokable;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitInstantiate(this);
    }

    public AstNode getInvokable() {
        return invokable;
    }

    @Override
    public Type getType() {
        return invokable.getType().getParameters().get(1);
    }
}
