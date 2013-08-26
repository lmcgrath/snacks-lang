package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.compiler.AstFactory.*;
import static snacks.lang.compiler.SyntaxFactory.importId;
import static snacks.lang.compiler.SyntaxFactory.qid;
import static snacks.lang.compiler.Type.*;

import java.util.*;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.*;
import snacks.lang.compiler.syntax.*;
import snacks.lang.compiler.syntax.Result;

public class Translator implements SyntaxVisitor {

    private final String module;
    private final Deque<SymbolEnvironment> environments;
    private final Deque<List<AstNode>> collections;
    private final Map<String, Locator> aliases;
    private final List<String> wildcardImports;
    private AstNode result;

    public Translator(SymbolEnvironment environment, String module) {
        this.module = module;
        this.environments = new ArrayDeque<>(asList(environment));
        this.collections = new ArrayDeque<>();
        this.aliases = new HashMap<>();
        this.wildcardImports = new ArrayList<>();
        this.wildcardImports.add("snacks/lang");
    }

    public List<AstNode> acceptCollection() {
        return collections.pop();
    }

    public void addAlias(String alias, Locator locator) {
        aliases.put(alias, locator);
    }

    public void addWildcardImport(String wildcardImport) {
        wildcardImports.add(wildcardImport);
    }

    public void beginCollection() {
        collections.push(new ArrayList<AstNode>());
    }

    public void collect(AstNode node) {
        collections.peek().add(node);
    }

    public Type createVariable() {
        return environment().createVariable();
    }

    public void define(Variable variable) throws SnacksException {
        define(variable.getLocator(), variable.getType());
    }

    public void define(Locator locator, Type type) throws SnacksException {
        environment().define(new Reference(locator, type));
    }

    public void enterScope() {
        environments.push(environment().extend());
    }

    public SymbolEnvironment environment() {
        return environments.peek();
    }

    public void generify(Type type) {
        environment().generify(type);
    }

    public String getModule() {
        return module;
    }

    public void leaveScope() {
        environments.pop();
    }

    public Reference reference(String value) throws SnacksException {
        Locator locator = new VariableLocator(value);
        if (!environment().isDefined(locator)) {
            if (aliases.containsKey(value)) {
                locator = aliases.get(value);
            } else {
                locator = findWildcard(value);
            }
        }
        return new Reference(locator, environment().typeOf(locator));
    }

    public void register(String name, Type type) throws SnacksException {
        Locator locator = locator(module, name);
        addAlias(name, locator);
        environment().define(new Reference(locator, type));
    }

    public void specialize(Type type) throws SnacksException {
        environment().specialize(type);
    }

    public Set<AstNode> translateModule(Symbol node) throws SnacksException {
        beginCollection();
        translate(node);
        return new HashSet<>(acceptCollection());
    }

    @Override
    public void visitAccessExpression(AccessExpression node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitAnnotated(Annotated node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitAnnotation(Annotation node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitApplyExpression(ApplyExpression node) throws SnacksException {
        result = applyFunction(
            translate(node.getExpression()),
            translate(node.getArgument())
        );
    }

    @Override
    public void visitArgument(Argument node) throws SnacksException {
        result = var(node.getName(), translateType(node.getType()));
    }

    @Override
    public void visitBinaryExpression(BinaryExpression node) throws SnacksException {
        result = applyFunction(
            applyFunction(
                reference(node.getOperator()),
                translate(node.getLeft())
            ),
            translate(node.getRight())
        );
    }

    @Override
    public void visitBlock(Block node) throws SnacksException {
        List<AstNode> elements = new ArrayList<>();
        enterScope();
        for (Symbol element : node.getElements()) {
            elements.add(translate(element));
        }
        leaveScope();
        if (elements.size() == 1) {
            result = elements.get(0);
        } else {
            result = sequence(elements);
        }
    }

    @Override
    public void visitBooleanLiteral(BooleanLiteral node) throws SnacksException {
        result = constant(node.getValue());
    }

    @Override
    public void visitCharacterLiteral(CharacterLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitConditional(Conditional node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitDeclaration(Declaration node) throws SnacksException {
        DeclaredExpression declaration = declaration(getModule(), node.getName(), translate(node.getBody()));
        register(declaration.getName(), declaration.getType());
        collect(declaration);
    }

    @Override
    public void visitDefaultCase(DefaultCase node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral node) throws SnacksException {
        result = constant(node.getValue());
    }

    @Override
    public void visitEmbraceCase(EmbraceCase node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitEnsureCase(EnsureCase node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitExceptional(Exceptional node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitFalsyCase(FalsyCase node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitFromImport(FromImport node) throws SnacksException {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        for (Symbol s : node.getSubImports()) {
            SubImport sub = (SubImport) s;
            translate(importId(qid(identifier, sub.getExpression()), sub.getAlias()));
        }
    }

    @Override
    public void visitFunctionLiteral(FunctionLiteral node) throws SnacksException {
        AstNode function = applyLambda(node, node.getArgument());
        Symbol typeNode = node.getType();
        if (typeNode != null) {
            Type resultType = translateType(typeNode);
            if (!unifyFunctionResult(function.getType(), resultType)) {
                throw new TypeException(
                    "Function type " + function.getType() + " not compatible with declared result type " + resultType
                );
            }
        }
        result = function;
    }

    @Override
    public void visitHurl(Hurl node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitIdentifier(Identifier node) throws SnacksException {
        result = reference(node.getValue());
    }

    @Override
    public void visitImport(Import node) throws SnacksException {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        List<String> segments = identifier.getSegments();
        Locator locator = locator(join(segments.subList(0, segments.size() - 1), '/'), identifier.getLastSegment());
        addAlias(node.getAlias(), locator);
    }

    @Override
    public void visitIndexExpression(IndexExpression node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitInstantiableLiteral(InstantiableLiteral node) throws SnacksException {
        result = instantiable(translate(node.getExpression()));
    }

    @Override
    public void visitInstantiationExpression(InstantiationExpression node) throws SnacksException {
        result = instantiate(translate(node.getExpression()));
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral node) throws SnacksException {
        result = constant(node.getValue());
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitListLiteral(ListLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitLoop(Loop node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitMapEntry(MapEntry node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitMapLiteral(MapLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitModule(Module node) throws SnacksException {
        for (Symbol element : node.getElements()) {
            translate(element);
        }
    }

    @Override
    public void visitNothingLiteral(NothingLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitQualifiedIdentifier(QualifiedIdentifier node) throws SnacksException {
        List<String> segments = node.getSegments();
        if (segments.size() > 1) {
            throw new UnsupportedOperationException(); // TODO
        }
        result = reference(segments.get(0));
    }

    @Override
    public void visitRegexLiteral(RegexLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitResult(Result node) throws SnacksException {
        result = result(translate(node.getExpression()));
    }

    @Override
    public void visitSetLiteral(SetLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitStringInterpolation(StringInterpolation node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitStringLiteral(StringLiteral node) throws SnacksException {
        result = constant(node.getValue());
    }

    @Override
    public void visitSubImport(SubImport node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitSymbolLiteral(SymbolLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitTruthyCase(TruthyCase node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitTupleLiteral(TupleLiteral node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitTypeSpec(TypeSpec node) throws SnacksException {
        result = translate(node.getType());
    }

    @Override
    public void visitUsing(Using node) throws SnacksException {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitVar(Var node) throws SnacksException {
        Type defVarType = createVariable();
        define(locator(node.getName()), defVarType);
        specialize(defVarType);
        AstNode value = translate(node.getValue());
        Type defActualType = value.getType();
        defVarType.unify(defActualType);
        generify(defVarType);
        result = var(node.getName(), value);
    }

    @Override
    public void visitWildcardImport(WildcardImport node) throws SnacksException {
        addWildcardImport(join(((QualifiedIdentifier) node.getModule()).getSegments(), '/'));
    }

    private AstNode applyFunction(AstNode expression, AstNode argument) throws TypeException {
        Type functionType = expression.getType();
        Type argumentType = argument.getType();
        Type constrainedArgumentType = argumentType.recompose(functionType, environment());
        List<Type> allowedTypes = new ArrayList<>();
        List<Type> functionTypesQueue = new LinkedList<>(functionType.decompose());
        for (Type argumentSubType : constrainedArgumentType.decompose()) {
            List<Type> allowedResultTypes = new ArrayList<>();
            for (Type functionSubType : functionTypesQueue) {
                Type resultType = createVariable();
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

    private AstNode applyLambda(FunctionLiteral node, Symbol argument) throws SnacksException {
        enterScope();
        Variable variable = translateAs(argument, Variable.class);
        define(variable);
        specialize(variable.getType());
        AstNode body = translate(node.getBody());
        leaveScope();
        List<Type> allowedLambdaTypes = new ArrayList<>();
        for (Type argumentSubType : variable.getType().decompose()) {
            enterScope();
            define(variable.getLocator(), argumentSubType);
            specialize(variable.getType());
            Type bodyType = translate(node.getBody()).getType();
            leaveScope();
            for (Type bodySubType : bodyType.decompose()) {
                allowedLambdaTypes.add(func(argumentSubType, bodySubType));
            }
        }
        return new Function(variable.getName(), body, set(allowedLambdaTypes));
    }

    private Locator findWildcard(String value) throws UndefinedSymbolException {
        for (String module : wildcardImports) {
            if (environment().isDefined(locator(module, value))) {
                return locator(module, value);
            }
        }
        throw new UndefinedSymbolException("Symbol '" + value + "' is undefined");
    }

    private AstNode translate(Visitable node) throws SnacksException {
        node.accept(this);
        return result;
    }

    private AstNode translate(Symbol node) throws SnacksException {
        return translate((Visitable) node);
    }

    private <T extends AstNode> T translateAs(Symbol node, Class<T> type) throws SnacksException {
        return type.cast(translate(node));
    }

    private Type translateType(Symbol node) throws SnacksException {
        if (node == null) {
            return createVariable();
        } else {
            return translate(node).getType();
        }
    }

    private boolean unifyFunctionResult(Type functionType, Type declaredResultType) throws SnacksException {
        if (functionType.decompose().size() == 1) {
            Type actualResultType = result(functionType);
            return actualResultType.unify(declaredResultType);
        } else {
            return false;
        }
    }
}
