package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.compiler.AstFactory.constant;
import static snacks.lang.compiler.AstFactory.declaration;
import static snacks.lang.compiler.AstFactory.locator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.AstNode;
import snacks.lang.compiler.ast.DeclaredExpression;
import snacks.lang.compiler.ast.Locator;
import snacks.lang.compiler.syntax.*;

public class Translator implements SyntaxVisitor<AstNode, TranslatorState> {

    private final SymbolEnvironment environment;

    public Translator(SymbolEnvironment environment) {
        this.environment = environment;
    }

    public TranslatorState createState(String module) {
        return new TranslatorState(environment, module);
    }

    public Set<AstNode> translate(TranslatorState state, Symbol node) throws SnacksException {
        state.beginCollection();
        translate(node, state);
        return new HashSet<>(state.acceptCollection());
    }

    public Set<AstNode> translate(String module, Symbol node) throws SnacksException {
        return translate(createState(module), node);
    }

    @Override
    public AstNode visitAccessExpression(AccessExpression node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitAnnotated(Annotated node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitAnnotation(Annotation node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitArgument(Argument node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitArgumentsExpression(ArgumentsExpression node, TranslatorState state) throws SnacksException {
        AstNode function = translate(node.getExpression(), state);
        List<AstNode> arguments = new ArrayList<>();
        for (Symbol n : node.getArguments()) {
            arguments.add(translate(n, state));
        }
        return state.resolve(function, arguments);
    }

    @Override
    public AstNode visitBinaryExpression(BinaryExpression node, TranslatorState state) throws SnacksException {
        AstNode function = state.reference(node.getOperator());
        List<AstNode> arguments = asList(
            translate(node.getLeft(), state),
            translate(node.getRight(), state)
        );
        return state.resolve(function, arguments);
    }

    @Override
    public AstNode visitBlock(Block node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitBooleanLiteral(BooleanLiteral node, TranslatorState state) throws SnacksException {
        return constant(node.getValue());
    }

    @Override
    public AstNode visitCharacterLiteral(CharacterLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitConditional(Conditional node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitDeclaration(Declaration node, TranslatorState state) throws SnacksException {
        DeclaredExpression declaration = declaration(state.getModule(), node.getName(), translate(node.getBody(), state));
        state.register(declaration.getName(), declaration.getType());
        state.collect(declaration);
        return null;
    }

    @Override
    public AstNode visitDefaultCase(DefaultCase node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitDoubleLiteral(DoubleLiteral node, TranslatorState state) throws SnacksException {
        return constant(node.getValue());
    }

    @Override
    public AstNode visitEmbraceCase(EmbraceCase node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitEnsureCase(EnsureCase node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitExceptional(Exceptional node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitFalsyCase(FalsyCase node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitFromImport(FromImport node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitFunctionLiteral(FunctionLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitHurl(Hurl node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitIdentifier(Identifier node, TranslatorState state) throws SnacksException {
        return state.reference(node.getValue());
    }

    @Override
    public AstNode visitImport(Import node, TranslatorState state) throws SnacksException {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        List<String> segments = identifier.getSegments();
        Locator locator = locator(join(segments.subList(0, segments.size() - 1), '/'), identifier.getLastSegment());
        state.addAlias(node.getAlias(), locator);
        return null;
    }

    @Override
    public AstNode visitIndexExpression(IndexExpression node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitIntegerLiteral(IntegerLiteral node, TranslatorState state) throws SnacksException {
        return constant(node.getValue());
    }

    @Override
    public AstNode visitIteratorLoop(IteratorLoop node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitListLiteral(ListLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitLoop(Loop node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitMapEntry(MapEntry node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitMapLiteral(MapLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitModule(Module node, TranslatorState state) throws SnacksException {
        for (Symbol element : node.getElements()) {
            translate(element, state);
        }
        return null;
    }

    @Override
    public AstNode visitNothingLiteral(NothingLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitQualifiedIdentifier(QualifiedIdentifier node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitRegexLiteral(RegexLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitResult(Result node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitSetLiteral(SetLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitStringInterpolation(StringInterpolation node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitStringLiteral(StringLiteral node, TranslatorState state) throws SnacksException {
        return constant(node.getValue());
    }

    @Override
    public AstNode visitSubImport(SubImport node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitSymbolLiteral(SymbolLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitTruthyCase(TruthyCase node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitTupleLiteral(TupleLiteral node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitTypeSpec(TypeSpec node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitUnaryExpression(UnaryExpression node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitUsing(Using node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitVar(Var node, TranslatorState state) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public AstNode visitWildcardImport(WildcardImport node, TranslatorState state) throws SnacksException {
        state.addWildcardImport(join(((QualifiedIdentifier) node.getModule()).getSegments(), '/'));
        return null;
    }

    private AstNode translate(Visitable node, TranslatorState state) throws SnacksException {
        return node.accept(this, state);
    }

    private AstNode translate(Symbol node, TranslatorState state) throws SnacksException {
        return translate((Visitable) node, state);
    }
}
