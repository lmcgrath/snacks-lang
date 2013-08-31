package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import beaver.Symbol;
import snacks.lang.util.PrinterState;

public class SyntaxPrinter implements SyntaxVisitor {

    private final PrinterState state;

    public SyntaxPrinter(PrintStream out) {
        state = new PrinterState(out);
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
    public void visitAnnotated(Annotated node) {
        print(node.getExpression());
        for (Symbol annotation : node.getAnnotations()) {
            print(annotation);
        }
    }

    @Override
    public void visitAnnotation(Annotation node) {
        print(node.getName());
        print(node.getValue());
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
    public void visitCharacterLiteral(CharacterLiteral node) {
        value("'" + node.getValue() + "'");
    }

    @Override
    public void visitConditional(Conditional node) {
        for (Symbol element : node.getCases()) {
            print(element);
        }
    }

    @Override
    public void visitDeclaration(Declaration node) {
        print("name: " + node.getName());
        print(node.getBody());
    }

    @Override
    public void visitDefaultCase(DefaultCase node) {
        print(node.getExpression());
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
    public void visitEnsureCase(EnsureCase node) {
        print(node.getExpression());
    }

    @Override
    public void visitExceptional(Exceptional node) {
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
    public void visitHurl(Hurl node) {
        print(node.getExpression());
    }

    @Override
    public void visitIdentifier(Identifier node) {
        value(node.getValue());
    }

    @Override
    public void visitImport(Import node) {
        print("module: " + node.getModule());
        print("alias: " + node.getAlias());
    }

    @Override
    public void visitIndexExpression(IndexExpression node) {
        for (Symbol argument : node.getArguments()) {
            print(argument);
        }
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral node) {
        value(node.getValue());
    }

    @Override
    public void visitInvokableLiteral(InvokableLiteral node) {
        value(node.getExpression());
    }

    @Override
    public void visitInvocation(Invocation node) {
        print(node.getExpression());
    }

    @Override
    public void visitIsExpression(IsExpression node) {
        print(node.getLeft());
        print(node.getRight());
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) {
        print("variable: " + node.getVariable());
        print(node.getExpression());
        print(node.getAction());
        print(node.getDefaultCase());
    }

    @Override
    public void visitListLiteral(ListLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitLoop(Loop node) {
        print(node.getLoopCase());
        print(node.getDefaultCase());
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
    public void visitModule(Module node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitNothingLiteral(NothingLiteral node) {
    }

    @Override
    public void visitQualifiedIdentifier(QualifiedIdentifier node) {
        print("identifier: " + node);
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
    public void visitConditionCase(ConditionCase node) {
        print(node.getCondition());
        print(node.getExpression());
    }

    @Override
    public void visitTupleLiteral(TupleLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitTypeSpec(TypeSpec node) {
        print(node.getType());
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
            print((Visitable) node);
        }
    }

    private void print(Visitable node) {
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
