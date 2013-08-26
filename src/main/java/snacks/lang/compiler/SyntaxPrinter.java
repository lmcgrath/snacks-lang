package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.syntax.*;
import snacks.lang.util.PrinterState;

public class SyntaxPrinter implements SyntaxVisitor {

    private final PrinterState state;

    public SyntaxPrinter(PrintStream out) {
        state = new PrinterState(out);
    }

    public void print(Object node) {
        try {
            print((Symbol) node);
        } catch (SnacksException exception) {
            state.print(exception);
        }
    }

    @Override
    public void visitAccessExpression(AccessExpression node) throws SnacksException {
        print(node.getExpression());
        state.println("property: " + node.getProperty());
    }

    @Override
    public void visitAnnotated(Annotated node) throws SnacksException {
        print(node.getExpression());
        for (Symbol annotation : node.getAnnotations()) {
            print(annotation);
        }
    }

    @Override
    public void visitAnnotation(Annotation node) throws SnacksException {
        print(node.getName());
        print(node.getValue());
    }

    @Override
    public void visitApplyExpression(ApplyExpression node) throws SnacksException {
        print(node.getExpression());
        print(node.getArgument());
    }

    @Override
    public void visitArgument(Argument node) throws SnacksException {
        state.println("name: " + node.getName());
        print(node.getType());
    }

    @Override
    public void visitBinaryExpression(BinaryExpression node) throws SnacksException {
        state.println("operator: " + node.getOperator());
        print(node.getLeft());
        print(node.getRight());
    }

    @Override
    public void visitBlock(Block node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitCharacterLiteral(CharacterLiteral node) throws SnacksException {
        value("'" + node.getValue() + "'");
    }

    @Override
    public void visitConditional(Conditional node) throws SnacksException {
        for (Symbol element : node.getCases()) {
            print(element);
        }
    }

    @Override
    public void visitDeclaration(Declaration node) throws SnacksException {
        state.begin(node);
        state.println("name: " + node.getName());
        print(node.getBody());
        state.end();
    }

    @Override
    public void visitDefaultCase(DefaultCase node) throws SnacksException {
        print(node.getExpression());
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitEmbraceCase(EmbraceCase node) throws SnacksException {
        state.println("argument: " + node.getArgument());
        print(node.getType());
        print(node.getExpression());
    }

    @Override
    public void visitEnsureCase(EnsureCase node) throws SnacksException {
        print(node.getExpression());
    }

    @Override
    public void visitExceptional(Exceptional node) throws SnacksException {
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
    public void visitFalsyCase(FalsyCase node) throws SnacksException {
        print(node.getCondition());
        print(node.getExpression());
    }

    @Override
    public void visitFromImport(FromImport node) throws SnacksException {
        state.println("module: " + node.getModule());
        for (Symbol subImport : node.getSubImports()) {
            print(subImport);
        }
    }

    @Override
    public void visitFunctionLiteral(FunctionLiteral node) throws SnacksException {
        print(node.getArgument());
        print(node.getType());
        print(node.getBody());
    }

    @Override
    public void visitHurl(Hurl node) throws SnacksException {
        state.begin(node);
        print(node.getExpression());
        state.end();
    }

    @Override
    public void visitIdentifier(Identifier node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitImport(Import node) throws SnacksException {
        state.println("module: " + node.getModule());
        state.println("alias: " + node.getAlias());
    }

    @Override
    public void visitIndexExpression(IndexExpression node) throws SnacksException {
        for (Symbol argument : node.getArguments()) {
            print(argument);
        }
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitInstantiableLiteral(InstantiableLiteral node) throws SnacksException {
        value(node.getExpression());
    }

    @Override
    public void visitInstantiationExpression(InstantiationExpression node) throws SnacksException {
        print(node.getExpression());
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) throws SnacksException {
        state.println("variable: " + node.getVariable());
        print(node.getExpression());
        print(node.getAction());
        print(node.getDefaultCase());
    }

    @Override
    public void visitListLiteral(ListLiteral node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitLoop(Loop node) throws SnacksException {
        print(node.getLoopCase());
        print(node.getDefaultCase());
    }

    @Override
    public void visitMapEntry(MapEntry node) throws SnacksException {
        print(node.getKey());
        print(node.getValue());
    }

    @Override
    public void visitMapLiteral(MapLiteral node) throws SnacksException {
        for (Symbol entry : node.getEntries()) {
            print(entry);
        }
    }

    @Override
    public void visitModule(Module node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitNothingLiteral(NothingLiteral node) throws SnacksException {
    }

    @Override
    public void visitQualifiedIdentifier(QualifiedIdentifier node) throws SnacksException {
        state.println("identifier: " + node);
    }

    @Override
    public void visitRegexLiteral(RegexLiteral node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
        state.println("options: [" + join(node.getOptions(), ", ") + "]");
    }

    @Override
    public void visitResult(Result node) throws SnacksException {
        print(node.getExpression());
    }

    @Override
    public void visitSetLiteral(SetLiteral node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitStringInterpolation(StringInterpolation node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitStringLiteral(StringLiteral node) throws SnacksException {
        value('"' + escapeJava(node.getValue()) + '"');
    }

    @Override
    public void visitSubImport(SubImport node) throws SnacksException {
        state.println("expression: " + node.getExpression());
        state.println("alias: " + node.getAlias());
    }

    @Override
    public void visitSymbolLiteral(SymbolLiteral node) throws SnacksException {
        value(node.getValue());
    }

    @Override
    public void visitTruthyCase(TruthyCase node) throws SnacksException {
        print(node.getCondition());
        print(node.getExpression());
    }

    @Override
    public void visitTupleLiteral(TupleLiteral node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element);
        }
    }

    @Override
    public void visitTypeSpec(TypeSpec node) throws SnacksException {
        print(node.getType());
    }

    @Override
    public void visitUsing(Using node) throws SnacksException {
        if (node.getName() != null) {
            state.println("name: " + node.getName());
        }
        print(node.getExpression());
    }

    @Override
    public void visitVar(Var node) throws SnacksException {
        state.println("name: " + node.getName());
        print(node.getValue());
    }

    @Override
    public void visitWildcardImport(WildcardImport node) throws SnacksException {
        state.println("module: " + node.getModule());
    }

    private void print(Symbol node) throws SnacksException {
        if (node != null) {
            print((Visitable) node);
        }
    }

    private void print(Visitable node) throws SnacksException {
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
