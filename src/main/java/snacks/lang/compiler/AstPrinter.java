package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.io.PrintStream;
import java.util.Set;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.*;
import snacks.lang.util.PrinterState;

public class AstPrinter implements AstVisitor<Void, PrinterState> {

    public void print(Set<AstNode> nodes, PrintStream out) {
        PrinterState state = new PrinterState(out);
        try {
            for (AstNode node : nodes) {
                print(node, state);
            }
        } catch (SnacksException exception) {
            exception.printStackTrace(out);
        }
    }

    @Override
    public Void visitApply(Apply node, PrinterState state) throws SnacksException {
        print(node.getFunction(), state);
        print(node.getArgument(), state);
        return null;
    }

    @Override
    public Void visitArgument(Variable node, PrinterState state) throws SnacksException {
        state.println("name: " + node.getName());
        state.println("type: " + node.getType());
        return null;
    }

    @Override
    public Void visitBooleanConstant(BooleanConstant node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitDeclaredExpression(DeclaredExpression node, PrinterState state) throws SnacksException {
        state.println("name: '" + node.getName() + "'");
        print(node.getBody(), state);
        return null;
    }

    @Override
    public Void visitDoubleConstant(DoubleConstant node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitFunction(Function node, PrinterState state) throws SnacksException {
        state.println("type: " + node.getType());
        state.println("variable: " + node.getVariable());
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitIntegerConstant(IntegerConstant node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitInvokable(Instantiable node, PrinterState state) throws SnacksException {
        print(node.getBody(), state);
        return null;
    }

    @Override
    public Void visitInvoke(Instantiate instantiate, PrinterState state) throws SnacksException {
        print(instantiate.getInvokable(), state);
        return null;
    }

    @Override
    public Void visitReference(Reference node, PrinterState state) throws SnacksException {
        state.println("locator: '" + node.getLocator() + "'");
        return null;
    }

    @Override
    public Void visitSequence(Sequence sequence, PrinterState state) throws SnacksException {
        for (AstNode element : sequence.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitStringConstant(StringConstant node, PrinterState state) throws SnacksException {
        value("\"" + escapeJava(node.getValue()) + "\"", state);
        return null;
    }

    @Override
    public Void visitVariableDeclaration(VariableDeclaration node, PrinterState state) throws SnacksException {
        value("name: " + node.getName(), state);
        print(node.getValue(), state);
        return null;
    }

    private void print(AstNode node, PrinterState state) throws SnacksException {
        state.begin(node);
        state.println("type: " + node.getType());
        node.accept(this, state);
        state.end();
    }

    private void value(Object value, PrinterState state) {
        state.println("value: " + value);
    }
}
