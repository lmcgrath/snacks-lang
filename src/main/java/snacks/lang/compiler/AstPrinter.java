package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.io.PrintStream;
import java.util.Set;
import snacks.lang.compiler.ast.*;
import snacks.lang.util.PrinterState;

public class AstPrinter implements AstVisitor {

    private final PrinterState state;

    public AstPrinter(PrintStream out) {
        state = new PrinterState(out);
    }

    public void print(Set<AstNode> nodes) {
        for (AstNode node : nodes) {
            print(node);
        }
    }

    @Override
    public void visitApply(Apply node) {
        print(node.getFunction());
        print(node.getArgument());
    }

    @Override
    public void visitBooleanConstant(BooleanConstant node) {
        value(node.getValue());
    }

    @Override
    public void visitClosure(Closure node) {
        state.println("type: " + node.getType());
        state.println("variable: " + node.getVariable());
        print(node.getBody());
    }

    @Override
    public void visitClosureLocator(ClosureLocator locator) {
        state.println("module: " + locator.getModule());
        state.println("name: " + locator.getName());
        state.println("environment: " + locator.getEnvironment());
    }

    @Override
    public void visitDeclarationLocator(ExpressionLocator locator) {
        value("module: " + locator.getModule());
        value("name: " + locator.getName());
    }

    @Override
    public void visitDeclaredArgument(DeclaredArgument node) {
        state.println("name: " + node.getName());
        state.println("type: " + node.getType());
    }

    @Override
    public void visitDeclaredExpression(DeclaredExpression node) {
        state.println("name: '" + node.getName() + "'");
        print(node.getBody());
    }

    @Override
    public void visitDoubleConstant(DoubleConstant node) {
        value(node.getValue());
    }

    @Override
    public void visitExpressionConstant(ExpressionConstant node) {
        print(node.getValue());
    }

    @Override
    public void visitFunction(Function node) {
        state.println("type: " + node.getType());
        state.println("variable: " + node.getVariable());
        print(node.getBody());
    }

    @Override
    public void visitIntegerConstant(IntegerConstant node) {
        value(node.getValue());
    }

    @Override
    public void visitVoidFunction(VoidFunction node) {
        print(node.getBody());
    }

    @Override
    public void visitVoidApply(VoidApply node) {
        print(node.getInstantiable());
    }

    @Override
    public void visitReference(Reference node) {
        state.println("locator: '" + node.getLocator() + "'");
    }

    @Override
    public void visitResult(Result node) {
        print(node.getValue());
    }

    @Override
    public void visitSequence(Sequence sequence) {
        for (AstNode element : sequence.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitStringConstant(StringConstant node) {
        value("\"" + escapeJava(node.getValue()) + "\"");
    }

    @Override
    public void visitVariableDeclaration(VariableDeclaration node) {
        value("name: " + node.getName());
        print(node.getValue());
    }

    @Override
    public void visitVariableLocator(VariableLocator locator) {
        value("name: " + locator.getName());
    }

    private void print(AstNode node) {
        state.begin(node);
        state.println("type: " + node.getType());
        node.accept(this);
        state.end();
    }

    private void value(Object value) {
        state.println("value: " + value);
    }
}
