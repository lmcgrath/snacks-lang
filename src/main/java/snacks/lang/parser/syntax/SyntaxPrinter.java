package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringUtils.join;

import java.io.PrintStream;
import java.util.Collection;
import beaver.Symbol;
import snacks.lang.Operator;
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
    public void visitAndExpression(AndExpression node) {
        print(node.getLeft());
        print(node.getRight());
    }

    @Override
    public void visitAnyMatcher(AnyMatcher node) {
        // intentionally empty
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
    public void visitCaptureMatcher(CaptureMatcher node) {
        print("variable: " + node.getVariable());
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
    public void visitConstantDeclaration(ConstantDeclaration node) {
        print("name: " + node.getName());
    }

    @Override
    public void visitConstantMatcher(ConstantMatcher node) {
        print(node.getConstant());
    }

    @Override
    public void visitConstructorExpression(ConstructorExpression node) {
        print(node.getConstructor());
        for (Symbol argument : node.getArguments()) {
            print(argument);
        }
    }

    @Override
    public void visitConstructorMatcher(ConstructorMatcher node) {
        print(node.getConstructor());
        for (Symbol argumentMatcher : node.getArgumentMatchers()) {
            print(argumentMatcher);
        }
    }

    @Override
    public void visitContinueExpression(ContinueExpression node) {
        // intentionally empty
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
    public void visitExpressionDeclaration(ExpressionDeclaration node) {
        print("name: " + node.getName());
        print(node.getBody());
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
    public void visitInvokableLiteral(InvokableLiteral node) {
        value(node.getBody());
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
    public void visitNamedPattern(NamedPattern node) {
        print("name: " + node.getName());
        print(node.getPattern());
    }

    @Override
    public void visitPatternMatcher(PatternMatcher node) {
        for (Symbol argument : node.getMatchers()) {
            print(argument);
        }
        print(node.getBody());
    }

    @Override
    public void visitNopExpression(NopExpression node) {
        // intentionally empty
    }

    @Override
    public void visitOperatorDeclaration(OperatorDeclaration node) {
        Operator operator = node.getOperator();
        print("name: `" + operator.getName() + "`");
        print("fixity: " + operator.getFixity());
        print("precedence: " + operator.getPrecedence());
    }

    @Override
    public void visitOrExpression(OrExpression node) {
        print(node.getLeft());
        print(node.getRight());
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
    public void visitPropertyMatcher(PropertyMatcher node) {
        print("name: " + node.getName());
        print(node.getMatcher());
    }

    @Override
    public void visitProtocolDeclaration(ProtocolDeclaration node) {
        print("name: " + node.getName());
        print("arguments: " + node.getArguments());
        print("members:");
        state.begin();
        print(node.getMembers());
        state.end();
    }

    @Override
    public void visitProtocolImplementation(ProtocolImplementation node) {
        print("name: " + node.getName());
        print("arguments:");
        state.begin();
        print(node.getArguments());
        state.end();
        print("members:");
        state.begin();
        print(node.getMembers());
        state.end();
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
    public void visitQuotedOperator(QuotedOperator node) {
        print("operator: " + node.getName());
    }

    @Override
    public void visitRecordDeclaration(RecordDeclaration node) {
        print("name: " + node.getName());
        print(node.getProperties());
    }

    @Override
    public void visitRecordMatcher(RecordMatcher node) {
        print(node.getConstructor());
        for (Symbol propertyMatcher : node.getPropertyMatchers()) {
            print(propertyMatcher);
        }
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
        print(node.getVariants());
    }

    @Override
    public void visitTypeReference(TypeReference node) {
        print(node.getType());
        for (Symbol parameter : node.getArguments()) {
            print(parameter);
        }
    }

    @Override
    public void visitTypeSpec(TypeSpec node) {
        print(node.getName());
    }

    @Override
    public void visitTypeVariable(TypeVariable node) {
        print("name: " + node.getName());
    }

    @Override
    public void visitUnitLiteral(UnitLiteral node) {
        print("unit: ()");
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
