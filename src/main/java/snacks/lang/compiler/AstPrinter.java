package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.io.PrintStream;
import java.util.Set;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.*;
import snacks.lang.util.PrinterState;

public class AstPrinter implements AstVisitor {

    private final PrinterState state;

    public AstPrinter(PrintStream out) {
        state = new PrinterState(out);
    }

    public void print(Set<AstNode> nodes) {
        try {
            for (AstNode node : nodes) {
                print(node);
            }
        } catch (SnacksException exception) {
            state.print(exception);
        }
    }

    @Override
    public void visitApply(Apply node) throws SnacksException {
        print(node.getFunction());
        print(node.getArgument());
    }

    @Override
    public void visitArgument(Variable node) throws SnacksException {
        state.println("name: " + node.getName());
        state.println("type: " + node.getType());
    }

    @Override
    public void visitBooleanConstant(BooleanConstant node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitDeclarationLocator(DeclarationLocator locator) throws SnacksException {
        value("module: " + locator.getModule());
        value("name: " + locator.getName());
    }

    @Override
    public void visitDeclaredExpression(DeclaredExpression node) throws SnacksException {
        state.println("name: '" + node.getName() + "'");
        print(node.getBody());
    }

    @Override
    public void visitDoubleConstant(DoubleConstant node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitFunction(Function node) throws SnacksException {
        state.println("type: " + node.getType());
        state.println("variable: " + node.getVariable());
        print(node.getExpression());
    }

    @Override
    public void visitIntegerConstant(IntegerConstant node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitInstantiable(Instantiable node) throws SnacksException {
        print(node.getBody());
    }

    @Override
    public void visitInstantiate(Instantiate instantiate) throws SnacksException {
        print(instantiate.getInvokable());
    }

    @Override
    public void visitReference(Reference node) throws SnacksException {
        state.println("locator: '" + node.getLocator() + "'");
    }

    @Override
    public void visitResult(Result node) throws SnacksException {
        print(node.getValue());
    }

    @Override
    public void visitSequence(Sequence sequence) throws SnacksException {
        for (AstNode element : sequence.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitStringConstant(StringConstant node) throws SnacksException {
        value("\"" + escapeJava(node.getValue()) + "\"");
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration node) throws SnacksException {
        value("name: " + node.getName());
        print(node.getValue());
    }

    @Override
    public void visitVariableLocator(VariableLocator locator) throws SnacksException {
        value("name: " + locator.getName());
    }

    private void print(AstNode node) throws SnacksException {
        state.begin(node);
        state.println("type: " + node.getType());
        node.accept(this);
        state.end();
    }

    private void value(Object value) {
        state.println("value: " + value);
    }
}
