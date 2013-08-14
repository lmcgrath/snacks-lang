package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;

public class ApplyNode implements SyntaxNode {

    private final SyntaxNode function;
    private final SyntaxNode argument;

    public ApplyNode(SyntaxNode function, SyntaxNode argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitApplyNode(this, state);
    }

    public SyntaxNode getArgument() {
        return argument;
    }

    public SyntaxNode getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return "(" + function + " " + argument + ")";
    }
}
