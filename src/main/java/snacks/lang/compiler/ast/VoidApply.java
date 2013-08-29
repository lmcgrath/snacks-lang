package snacks.lang.compiler.ast;

public class VoidApply implements AstNode {

    private final AstNode invokable;

    public VoidApply(AstNode invokable) {
        this.invokable = invokable;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitVoidApply(this);
    }

    public AstNode getInstantiable() {
        return invokable;
    }

    @Override
    public Type getType() {
        return invokable.getType().getParameters().get(1);
    }

    @Override
    public boolean isInvokable() {
        return false;
    }

    @Override
    public String toString() {
        return invokable + "()";
    }
}
