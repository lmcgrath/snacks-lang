package snacks.lang.ast;

import static java.lang.String.valueOf;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.io.PrintStream;
import java.util.Set;
import snacks.lang.util.PrinterState;

public class AstPrinter {

    private final PrinterState state;

    public AstPrinter(PrintStream out) {
        state = new PrinterState(out);
    }

    public void print(Set<AstNode> nodes) {
        for (AstNode node : nodes) {
            print(node);
        }
    }

    public void print(AstNode node) {
        state.begin(node);
        print("type: " + node.getType());
        node.print(this);
        state.end();
    }

    public void print(String object) {
        state.println(object);
    }

    public void printApply(Apply node) {
        print(node.getFunction());
        print(node.getArgument());
    }

    public void printAssign(Assign node) {
        print(node.getLeft());
        print(node.getRight());
    }

    public void printBooleanConstant(BooleanConstant node) {
        print("value: " + node.getValue());
    }

    public void printCharacterConstant(CharacterConstant node) {
        print("value: '" + escapeJava(valueOf(node.getValue())));
    }

    public void printClosure(Closure node) {
        print("type: " + node.getType());
        print("variable: " + node.getVariable());
        print(node.getBody());
    }

    public void printClosureLocator(ClosureLocator locator) {
        print("module: " + locator.getModule());
        print("name: " + locator.getName());
        print("environment: " + locator.getEnvironment());
    }

    public void printDeclarationLocator(DeclarationLocator locator) {
        print("module: " + locator.getModule());
        print("name: " + locator.getName());
    }

    public void printDeclaredArgument(DeclaredArgument node) {
        print("name: " + node.getName());
        print("type: " + node.getType());
    }

    public void printDeclaredExpression(DeclaredExpression node) {
        print("name: '" + node.getName() + "'");
        print(node.getBody());
    }

    public void printDoubleConstant(DoubleConstant node) {
        print("value: " + node.getValue());
    }

    public void printEmbrace(Embrace embrace) {
        print("variable: " + embrace.getVariable());
        print("type: " + embrace.getException());
        print(embrace.getBody());
    }

    public void printExceptional(Exceptional node) {
        print(node.getBegin());
        for (AstNode embrace : node.getEmbraces()) {
            print(embrace);
        }
        print(node.getEnsure());
    }

    public void printExpressionConstant(ExpressionConstant node) {
        print(node.getValue());
    }

    public void printFunction(Function node) {
        print("type: " + node.getType());
        print("variable: " + node.getVariable());
        print(node.getBody());
    }

    public void printGuardCase(GuardCase node) {
        print(node.getCondition());
        print(node.getExpression());
    }

    public void printGuardCases(GuardCases node) {
        for (AstNode n : node.getCases()) {
            print(n);
        }
    }

    public void printIntegerConstant(IntegerConstant node) {
        print("value: " + node.getValue());
    }

    public void printReference(Reference node) {
        print("locator: '" + node.getLocator() + "'");
    }

    public void printReferencesEqual(ReferencesEqual node) {
        print(node.getLeft());
        print(node.getRight());
    }

    public void printResult(Result node) {
        print(node.getValue());
    }

    public void printSequence(Sequence sequence) {
        for (AstNode element : sequence.getElements()) {
            print(element);
        }
    }

    public void printStringConstant(StringConstant node) {
        print("value: \"" + escapeJava(node.getValue()) + "\"");
    }

    public void printSymbolConstant(SymbolConstant node) {
        print("name: \"" + escapeJava(node.getName()) + "\"");
    }

    public void printVariableDeclaration(VariableDeclaration node) {
        print("name: " + node.getName());
    }

    public void printVariableLocator(VariableLocator locator) {
        print("name: " + locator.getName());
    }

    public void printVoidApply(VoidApply node) {
        print(node.getInstantiable());
    }

    public void printVoidFunction(VoidFunction node) {
        print(node.getBody());
    }
}
