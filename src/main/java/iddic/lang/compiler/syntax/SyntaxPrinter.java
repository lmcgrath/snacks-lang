package iddic.lang.compiler.syntax;

import java.io.PrintStream;
import iddic.lang.IddicException;
import iddic.lang.util.PrinterState;

public class SyntaxPrinter implements SyntaxVisitor<Void, PrinterState> {

    public void print(SyntaxNode node, PrintStream out) {
        try {
            node.accept(this, new PrinterState(out));
        } catch (IddicException exception) {
            exception.printStackTrace(out);
        }
    }

    @Override
    public Void visitApplyNode(ApplyNode node, PrinterState state) throws IddicException {
        state.begin(node);
        print(node.getFunction(), state);
        print(node.getArgument(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitBooleanNode(BooleanNode node, PrinterState state) throws IddicException {
        state.println(node);
        return null;
    }

    @Override
    public Void visitDoubleNode(DoubleNode node, PrinterState state) throws IddicException {
        state.println(node);
        return null;
    }

    @Override
    public Void visitIdentifierNode(IdentifierNode node, PrinterState state) throws IddicException {
        state.println(node);
        return null;
    }

    @Override
    public Void visitIntegerNode(IntegerNode node, PrinterState state) throws IddicException {
        state.println(node);
        return null;
    }

    @Override
    public Void visitNothingNode(NothingNode node, PrinterState state) throws IddicException {
        state.println(node);
        return null;
    }

    @Override
    public Void visitStringNode(StringNode node, PrinterState state) throws IddicException {
        state.println(node);
        return null;
    }

    private void print(SyntaxNode node, PrinterState state) throws IddicException {
        node.accept(this, state);
    }
}
