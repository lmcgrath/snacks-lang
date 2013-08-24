package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.compiler.AstFactory.apply;
import static snacks.lang.compiler.AstFactory.constant;
import static snacks.lang.compiler.AstFactory.declaration;
import static snacks.lang.compiler.AstFactory.locator;
import static snacks.lang.compiler.AstFactory.var;
import static snacks.lang.compiler.SyntaxFactory.importId;
import static snacks.lang.compiler.SyntaxFactory.qid;
import static snacks.lang.compiler.Type.func;
import static snacks.lang.compiler.Type.set;

import java.util.*;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.*;
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
        return var(node.getName(), translateType(node.getType(), state));
    }

    @Override
    public AstNode visitArgumentsExpression(ArgumentsExpression node, TranslatorState state) throws SnacksException {
        AstNode function = translate(node.getExpression(), state);
        state.beginCollection();
        for (Symbol argument : node.getArguments()) {
            state.collect(translate(argument, state));
        }
        return applyFunction(function, state.acceptCollection(), state);
    }

    @Override
    public AstNode visitBinaryExpression(BinaryExpression node, TranslatorState state) throws SnacksException {
        AstNode function = state.reference(node.getOperator());
        List<AstNode> arguments = asList(
            translate(node.getLeft(), state),
            translate(node.getRight(), state)
        );
        return applyFunction(function, arguments, state);
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
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        for (Symbol s : node.getSubImports()) {
            SubImport sub = (SubImport) s;
            translate(importId(qid(identifier, sub.getExpression()), sub.getAlias()), state);
        }
        return null;
    }

    @Override
    public AstNode visitFunctionLiteral(FunctionLiteral node, TranslatorState state) throws SnacksException {
        List<Symbol> arguments = node.getArguments();
        if (arguments.size() != 1) {
            throw new UnsupportedOperationException(); // TODO
        } else {
            return applyLambda(node, arguments, state);
        }
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
        List<String> segments = node.getSegments();
        if (segments.size() > 1) {
            throw new UnsupportedOperationException(); // TODO
        }
        return state.reference(segments.get(0));
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
        return translate(node.getType(), state);
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

    private AstNode applyFunction(AstNode function, List<AstNode> arguments, TranslatorState state) throws TypeException {
        AstNode expression = function;
        for (AstNode argument : arguments) {
            expression = applyFunction(expression, argument, state);
        }
        return expression;
    }

    private AstNode applyFunction(AstNode expression, AstNode argument, TranslatorState state) throws TypeException {
        Type functionType = expression.getType();
        Type argumentType = argument.getType();
        Type constrainedArgumentType = argumentType.recompose(functionType, state.environment());
        List<Type> allowedTypes = new ArrayList<>();
        List<Type> functionTypesQueue = new LinkedList<>(functionType.decompose());
        for (Type argumentSubType : constrainedArgumentType.decompose()) {
            List<Type> allowedResultTypes = new ArrayList<>();
            for (Type functionSubType : functionTypesQueue) {
                Type resultType = state.createVariable();
                if (func(argumentSubType, resultType).unify(functionSubType)) {
                    allowedResultTypes.add(resultType);
                }
            }
            allowedTypes.addAll(allowedResultTypes);
            functionTypesQueue.remove(0);
        }
        if (allowedTypes.isEmpty()) {
            throw new TypeException("Could not apply function " + functionType + " to argument " + argumentType);
        }
        argumentType.bind(constrainedArgumentType);
        return apply(expression, argument, set(allowedTypes));
    }

    private AstNode applyLambda(FunctionLiteral node, List<Symbol> arguments, TranslatorState state) throws SnacksException {
        state.enterScope();
        Variable variable = translateAs(arguments.get(0), state, Variable.class);
        state.define(variable);
        state.specialize(variable);
        AstNode body = translate(node.getBody(), state);
        state.leaveScope();
        List<Type> allowedLambdaTypes = new ArrayList<>();
        for (Type argumentSubType : variable.getType().decompose()) {
            state.enterScope();
            state.define(variable.getLocator(), argumentSubType);
            state.specialize(variable);
            Type bodyType = translate(node.getBody(), state).getType();
            state.leaveScope();
            for (Type bodySubType : bodyType.decompose()) {
                allowedLambdaTypes.add(func(argumentSubType, bodySubType));
            }
        }
        return new Function(variable.getName(), body, set(allowedLambdaTypes));
    }

    private AstNode translate(Visitable node, TranslatorState state) throws SnacksException {
        return node.accept(this, state);
    }

    private AstNode translate(Symbol node, TranslatorState state) throws SnacksException {
        return translate((Visitable) node, state);
    }

    private <T extends AstNode> T translateAs(Symbol node, TranslatorState state, Class<T> type) throws SnacksException {
        return type.cast(translate(node, state));
    }

    private Type translateType(Symbol node, TranslatorState state) throws SnacksException {
        if (node == null) {
            return state.createVariable();
        } else {
            return translate(node, state).getType();
        }
    }
}
