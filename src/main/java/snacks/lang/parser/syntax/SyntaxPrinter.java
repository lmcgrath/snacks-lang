package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import java.util.Collection;
import beaver.Symbol;
import snacks.lang.util.PrinterState;

public class SyntaxPrinter implements SyntaxVisitor {

    private final PrinterState state;

    public SyntaxPrinter(PrintStream out) {
        state = new PrinterState(out);
    }

    public void print(Collection<?> collection) {
        for (Object node : collection) {
            print(node);
        }
    }

    public void print(Object node) {
        print((Symbol) node);
    }

    @Override
    public void visitAccessExpression(AccessExpression node) {
        print(node.getExpression());
        print("property: " + node.getProperty());
    }

    @Override
    public void visitApplyExpression(ApplyExpression node) {
        print(node.getExpression());
        print(node.getArgument());
    }

    @Override
    public void visitArgument(Argument node) {
        print("name: " + node.getName());
        print(node.getType());
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpression node) {
        print(node.getTarget());
        print(node.getValue());
    }

    @Override
    public void visitBlock(Block node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral node) {
        value(node.getValue());
    }

    @Override
    public void visitBreakExpression(BreakExpression node) {
        // intentionally empty
    }

    @Override
    public void visitCharacterLiteral(CharacterLiteral node) {
        value("'" + node.getValue() + "'");
    }

    @Override
    public void visitConditionCase(ConditionCase node) {
        print(node.getCondition());
        print(node.getExpression());
    }

    @Override
    public void visitConditional(Conditional node) {
        for (Symbol element : node.getCases()) {
            print(element);
        }
    }

    @Override
    public void visitContinueExpression(ContinueExpression node) {
        // intentionally empty
    }

    @Override
    public void visitDeclaration(Declaration node) {
        print("name: " + node.getName());
        print(node.getBody());
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral node) {
        value(node.getValue());
    }

    @Override
    public void visitEmbraceCase(EmbraceCase node) {
        print("argument: " + node.getArgument());
        print(node.getType());
        print(node.getExpression());
    }

    @Override
    public void visitExceptional(ExceptionalExpression node) {
        for (Symbol useCase : node.getUseCases()) {
            print(useCase);
        }
        print(node.getExpression());
        for (Symbol embraceCase : node.getEmbraceCases()) {
            print(embraceCase);
        }
        print(node.getEnsureCase());
    }

    @Override
    public void visitFromImport(FromImport node) {
        print("module: " + node.getModule());
        for (Symbol subImport : node.getSubImports()) {
            print(subImport);
        }
    }

    @Override
    public void visitFunctionLiteral(FunctionLiteral node) {
        print(node.getArgument());
        print(node.getType());
        print(node.getBody());
    }

    @Override
    public void visitFunctionSignature(FunctionSignature node) {
        print(node.getArgument());
        print(node.getResult());
    }

    @Override
    public void visitHurl(HurlExpression node) {
        print(node.getExpression());
    }

    @Override
    public void visitIdentifier(Identifier node) {
        value(node.getName());
    }

    @Override
    public void visitImport(Import node) {
        print("module: " + node.getModule());
        print("alias: " + node.getAlias());
    }

    @Override
    public void visitInitializerExpression(InitializerExpression node) {
        print("constructor: " + node.getConstructor());
        print(node.getProperties());
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral node) {
        value(node.getValue());
    }

    @Override
    public void visitInvocation(Invocation node) {
        print(node.getExpression());
    }

    @Override
    public void visitInvokableLiteral(InvokableLiteral node) {
        value(node.getExpression());
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) {
        print("variable: " + node.getVariable());
        print(node.getExpression());
        print(node.getAction());
    }

    @Override
    public void visitListLiteral(ListLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitLoopExpression(LoopExpression node) {
        print(node.getCondition());
        print(node.getBody());
    }

    @Override
    public void visitMapEntry(MapEntry node) {
        print(node.getKey());
        print(node.getValue());
    }

    @Override
    public void visitMapLiteral(MapLiteral node) {
        for (Symbol entry : node.getEntries()) {
            print(entry);
        }
    }

    @Override
    public void visitMessage(Message node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitModule(Module node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitNopExpression(NopExpression node) {
        // intentionally empty
    }

    @Override
    public void visitOperator(Operator node) {
        print("name: `" + node.getName() + "`");
        print("fixity: " + node.getFixity());
        print("precedence: " + node.getPrecedence());
    }

    @Override
    public void visitPropertyDeclaration(PropertyDeclaration node) {
        print("name: " + node.getName());
        print(node.getType());
    }

    @Override
    public void visitPropertyExpression(PropertyExpression node) {
        print("name: " + node.getName());
        print(node.getValue());
    }

    @Override
    public void visitQualifiedIdentifier(QualifiedIdentifier node) {
        print("identifier: " + node);
    }

    @Override
    public void visitQuotedIdentifier(QuotedIdentifier node) {
        print("identifier: " + node.getName());
    }

    @Override
    public void visitRecordDeclaration(RecordDeclaration node) {
        print("name: " + node.getName());
        print(node.getProperties());
    }

    @Override
    public void visitRegexLiteral(RegexLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
        print("options: [" + join(node.getOptions(), ", ") + "]");
    }

    @Override
    public void visitResult(Result node) {
        print(node.getExpression());
    }

    @Override
    public void visitSetLiteral(SetLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitSignature(Signature node) {
        print(node.getIdentifier());
        print(node.getType());
    }

    @Override
    public void visitStringLiteral(StringLiteral node) {
        value('"' + escapeJava(node.getValue()) + '"');
    }

    @Override
    public void visitSubImport(SubImport node) {
        print("expression: " + node.getExpression());
        print("alias: " + node.getAlias());
    }

    @Override
    public void visitSymbolLiteral(SymbolLiteral node) {
        value(node.getValue());
    }

    @Override
    public void visitTupleLiteral(TupleLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitTupleSignature(TupleSignature node) {
        for (Symbol type : node.getTypes()) {
            print(type);
        }
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        print("name: " + node.getName());
        print(node.getDefinition());
    }

    @Override
    public void visitTypeSpec(TypeSpec node) {
        print(node.getType());
    }

    @Override
    public void visitTypeVariable(TypeVariable node) {
        print("name: " + node.getName());
    }

    @Override
    public void visitUsing(Using node) {
        if (node.getName() != null) {
            print("name: " + node.getName());
        }
        print(node.getExpression());
    }

    @Override
    public void visitVar(Var node) {
        print("name: " + node.getName());
        print(node.getValue());
    }

    @Override
    public void visitVarDeclaration(VarDeclaration node) {
        print("name: " + node);
    }

    @Override
    public void visitWildcardImport(WildcardImport node) {
        print("module: " + node.getModule());
    }

    private void print(String line) {
        state.println(line);
    }

    private void print(Symbol node) {
        if (node != null) {
            print((VisitableSymbol) node);
        }
    }

    private void print(VisitableSymbol node) {
        if (node != null) {
            state.begin(node);
            node.accept(this);
            state.end();
        }
    }

    private void value(Object value) {
        state.println("value: " + value);
    }
}
