package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.syntax.*;
import snacks.lang.util.PrinterState;

public class SyntaxPrinter implements SyntaxVisitor<Void, PrinterState> {

    public void print(Object node, PrintStream out) {
        try {
            print((Symbol) node, new PrinterState(out));
        } catch (SnacksException exception) {
            exception.printStackTrace(out);
        }
    }

    @Override
    public Void visitAccessExpression(AccessExpression node, PrinterState state) throws SnacksException {
        print(node.getExpression(), state);
        state.println("property: " + node.getProperty());
        return null;
    }

    @Override
    public Void visitAnnotated(Annotated node, PrinterState state) throws SnacksException {
        print(node.getExpression(), state);
        for (Symbol annotation : node.getAnnotations()) {
            print(annotation, state);
        }
        return null;
    }

    @Override
    public Void visitAnnotation(Annotation node, PrinterState state) throws SnacksException {
        print(node.getName(), state);
        print(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitArgument(Argument node, PrinterState state) throws SnacksException {
        state.println("name: " + node.getName());
        print(node.getType(), state);
        return null;
    }

    @Override
    public Void visitArgumentsExpression(ArgumentsExpression node, PrinterState state) throws SnacksException {
        print(node.getExpression(), state);
        for (Symbol argument : node.getArguments()) {
            print(argument, state);
        }
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression node, PrinterState state) throws SnacksException {
        state.println("operator: " + node.getOperator());
        print(node.getLeft(), state);
        print(node.getRight(), state);
        return null;
    }

    @Override
    public Void visitBlock(Block node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitBooleanLiteral(BooleanLiteral node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitCharacterLiteral(CharacterLiteral node, PrinterState state) throws SnacksException {
        value("'" + node.getValue() + "'", state);
        return null;
    }

    @Override
    public Void visitConditional(Conditional node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getCases()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration node, PrinterState state) throws SnacksException {
        state.begin(node);
        state.println("name: " + node.getName());
        print(node.getBody(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitDefaultCase(DefaultCase node, PrinterState state) throws SnacksException {
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitDoubleLiteral(DoubleLiteral node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitEmbraceCase(EmbraceCase node, PrinterState state) throws SnacksException {
        state.println("argument: " + node.getArgument());
        print(node.getType(), state);
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitEnsureCase(EnsureCase node, PrinterState state) throws SnacksException {
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitExceptional(Exceptional node, PrinterState state) throws SnacksException {
        for (Symbol useCase : node.getUseCases()) {
            print(useCase, state);
        }
        print(node.getExpression(), state);
        for (Symbol embraceCase : node.getEmbraceCases()) {
            print(embraceCase, state);
        }
        print(node.getEnsureCase(), state);
        return null;
    }

    @Override
    public Void visitFalsyCase(FalsyCase node, PrinterState state) throws SnacksException {
        print(node.getCondition(), state);
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitFromImport(FromImport node, PrinterState state) throws SnacksException {
        state.println("module: " + node.getModule());
        for (Symbol subImport : node.getSubImports()) {
            print(subImport, state);
        }
        return null;
    }

    @Override
    public Void visitFunctionLiteral(FunctionLiteral node, PrinterState state) throws SnacksException {
        for (Symbol argument : node.getArguments()) {
            print(argument, state);
        }
        print(node.getType(), state);
        print(node.getBody(), state);
        return null;
    }

    @Override
    public Void visitHurl(Hurl node, PrinterState state) throws SnacksException {
        state.begin(node);
        print(node.getExpression(), state);
        state.end();
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitImport(Import node, PrinterState state) throws SnacksException {
        state.println("module: " + node.getModule());
        state.println("alias: " + node.getAlias());
        return null;
    }

    @Override
    public Void visitIndexExpression(IndexExpression node, PrinterState state) throws SnacksException {
        for (Symbol argument : node.getArguments()) {
            print(argument, state);
        }
        return null;
    }

    @Override
    public Void visitIntegerLiteral(IntegerLiteral node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitIteratorLoop(IteratorLoop node, PrinterState state) throws SnacksException {
        state.println("variable: " + node.getVariable());
        print(node.getExpression(), state);
        print(node.getAction(), state);
        print(node.getDefaultCase(), state);
        return null;
    }

    @Override
    public Void visitListLiteral(ListLiteral node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitLoop(Loop node, PrinterState state) throws SnacksException {
        print(node.getLoopCase(), state);
        print(node.getDefaultCase(), state);
        return null;
    }

    @Override
    public Void visitMapEntry(MapEntry node, PrinterState state) throws SnacksException {
        print(node.getKey(), state);
        print(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitMapLiteral(MapLiteral node, PrinterState state) throws SnacksException {
        for (Symbol entry : node.getEntries()) {
            print(entry, state);
        }
        return null;
    }

    @Override
    public Void visitModule(Module node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitNothingLiteral(NothingLiteral node, PrinterState state) throws SnacksException {
        return null;
    }

    @Override
    public Void visitQualifiedIdentifier(QualifiedIdentifier node, PrinterState state) throws SnacksException {
        state.println("identifier: " + node);
        return null;
    }

    @Override
    public Void visitRegexLiteral(RegexLiteral node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        state.println("options: [" + join(node.getOptions(), ", ") + "]");
        return null;
    }

    @Override
    public Void visitResult(Result node, PrinterState state) throws SnacksException {
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitSetLiteral(SetLiteral node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitStringInterpolation(StringInterpolation node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitStringLiteral(StringLiteral node, PrinterState state) throws SnacksException {
        value('"' + escapeJava(node.getValue()) + '"', state);
        return null;
    }

    @Override
    public Void visitSubImport(SubImport node, PrinterState state) throws SnacksException {
        state.println("expression: " + node.getExpression());
        state.println("alias: " + node.getAlias());
        return null;
    }

    @Override
    public Void visitSymbolLiteral(SymbolLiteral node, PrinterState state) throws SnacksException {
        value(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitTruthyCase(TruthyCase node, PrinterState state) throws SnacksException {
        print(node.getCondition(), state);
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitTupleLiteral(TupleLiteral node, PrinterState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            print(element, state);
        }
        return null;
    }

    @Override
    public Void visitTypeSpec(TypeSpec node, PrinterState state) throws SnacksException {
        print(node.getType(), state);
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression node, PrinterState state) throws SnacksException {
        state.println("operator: " + node.getOperator());
        print(node.getOperand(), state);
        return null;
    }

    @Override
    public Void visitUsing(Using node, PrinterState state) throws SnacksException {
        if (node.getName() != null) {
            state.println("name: " + node.getName());
        }
        print(node.getExpression(), state);
        return null;
    }

    @Override
    public Void visitVar(Var node, PrinterState state) throws SnacksException {
        state.println("name: " + node.getName());
        print(node.getValue(), state);
        return null;
    }

    @Override
    public Void visitWildcardImport(WildcardImport node, PrinterState state) throws SnacksException {
        state.println("module: " + node.getModule());
        return null;
    }

    private void print(Symbol node, PrinterState state) throws SnacksException {
        if (node != null) {
            print((Visitable) node, state);
        }
    }

    private void print(Visitable node, PrinterState state) throws SnacksException {
        if (node != null) {
            state.begin(node);
            node.accept(this, state);
            state.end();
        }
    }

    private void value(Object value, PrinterState state) {
        state.println("value: " + value);
    }
}
