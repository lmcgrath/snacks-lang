package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import beaver.Symbol;
import snacks.lang.compiler.syntax.*;
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
        state.println("property: " + node.getProperty());
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
        state.println("name: " + node.getName());
        print(node.getType());
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
        state.begin(node);
        state.println("name: " + node.getName());
        print(node.getBody());
        state.end();
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
        state.println("argument: " + node.getArgument());
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
    public void visitFalsyCase(FalsyCase node) {
        print(node.getCondition());
        print(node.getExpression());
    }

    @Override
    public void visitFromImport(FromImport node) {
        state.println("module: " + node.getModule());
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
        state.begin(node);
        print(node.getExpression());
        state.end();
    }

    @Override
    public void visitIdentifier(Identifier node) {
        value(node.getValue());
    }

    @Override
    public void visitImport(Import node) {
        state.println("module: " + node.getModule());
        state.println("alias: " + node.getAlias());
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
    public void visitInstantiableLiteral(InstantiableLiteral node) {
        value(node.getExpression());
    }

    @Override
    public void visitInstantiationExpression(InstantiationExpression node) {
        print(node.getExpression());
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) {
        state.println("variable: " + node.getVariable());
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
        state.println("identifier: " + node);
    }

    @Override
    public void visitRegexLiteral(RegexLiteral node) {
        for (Symbol element : node.getElements()) {
            print(element);
        }
        state.println("options: [" + join(node.getOptions(), ", ") + "]");
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
    public void visitStringInterpolation(StringInterpolation node) {
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
        state.println("expression: " + node.getExpression());
        state.println("alias: " + node.getAlias());
    }

    @Override
    public void visitSymbolLiteral(SymbolLiteral node) {
        value(node.getValue());
    }

    @Override
    public void visitTruthyCase(TruthyCase node) {
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
            state.println("name: " + node.getName());
        }
        print(node.getExpression());
    }

    @Override
    public void visitVar(Var node) {
        state.println("name: " + node.getName());
        print(node.getValue());
    }

    @Override
    public void visitWildcardImport(WildcardImport node) {
        state.println("module: " + node.getModule());
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
