package snacks.lang.ast;

import static snacks.lang.Type.resultOf;

import snacks.lang.Type;

public class VoidApply extends AstNode {

    private final AstNode invokable;

    public VoidApply(AstNode invokable) {
        this.invokable = invokable;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printVoidApply(this);
    }

    public AstNode getInstantiable() {
        return invokable;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateVoidApply(this);
    }

    @Override
    public Type getType() {
        return resultOf(invokable.getType());
    }

    @Override
    public String toString() {
        return invokable + "()";
    }
}
