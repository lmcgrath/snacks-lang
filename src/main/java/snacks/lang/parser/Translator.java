package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.ast.AstFactory.*;
import static snacks.lang.Type.*;
import static snacks.lang.parser.syntax.SyntaxFactory.importId;
import static snacks.lang.parser.syntax.SyntaxFactory.qid;

import java.util.*;
import beaver.Symbol;
import snacks.lang.Type;
import snacks.lang.ast.*;
import snacks.lang.parser.syntax.*;
import snacks.lang.parser.syntax.Result;
import snacks.lang.parser.syntax.TupleLiteral;

public class Translator implements SyntaxVisitor {

    private final String module;
    private final Deque<SymbolEnvironment> environments;
    private final Set<AstNode> declarations;
    private final Map<String, Locator> aliases;
    private final List<String> wildcardImports;
    private final List<String> typeErrors;
    private final Map<Symbol, Locator> names;
    private AstNode result;
    private int functionLevel;
    private NameSequence currentName;

    public Translator(SymbolEnvironment environment, String module) {
        this.module = module;
        this.environments = new ArrayDeque<>(asList(environment));
        this.declarations = new HashSet<>();
        this.aliases = new HashMap<>();
        this.typeErrors = new ArrayList<>();
        this.names = new IdentityHashMap<>();
        this.wildcardImports = new ArrayList<>(asList("snacks.lang"));
    }

    public void addAlias(String alias, Locator locator) {
        aliases.put(alias, locator);
    }

    public void addWildcardImport(String wildcardImport) {
        wildcardImports.add(wildcardImport);
    }

    public Type createVariable() {
        return environment().createVariable();
    }

    public void define(Locator locator, Type type) {
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

    public Reference reference(String value) {
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

    public void register(String name, Type type) {
        Locator locator = locator(module, name);
        addAlias(name, locator);
        environment().define(new Reference(locator, type));
    }

    public void specialize(Type type) {
        environment().specialize(type);
    }

    public Set<AstNode> translateModule(Symbol node) {
        translate(node);
        return new HashSet<>(declarations);
    }

    @Override
    public void visitAccessExpression(AccessExpression node) {
        AstNode expression = translate(node.getExpression());
        String property = node.getProperty();
        for (Type type : expression.getType().decompose()) {
            for (Type parameter : type.getParameters()) {
                if (parameter.getName().equals(property)) {
                    result = access(expression, property, parameter.getParameters().get(0));
                    return;
                }
            }
        }
        throw new TypeException("Could not locate property '" + property + "' on type " + expression.getType());
    }

    @Override
    public void visitApplyExpression(ApplyExpression node) {
        AstNode function = translate(node.getExpression());
        AstNode argument = translate(node.getArgument());
        result = apply(function, argument, inferenceResultType(function.getType(), argument.getType()));
    }

    @Override
    public void visitArgument(Argument node) {
        DeclaredArgument argument = new DeclaredArgument(node.getName(), translateType(node.getType()));
        define(argument.getLocator(), argument.getType());
        specialize(argument.getType());
        result = argument;
    }

    @Override
    public void visitAssignmentExpression(AssignmentExpression node) {
        AstNode target = translate(node.getTarget());
        AstNode value = translate(node.getValue());
        Type targetType = target.getType();
        specialize(targetType);
        Type valueType = value.getType();
        if (!targetType.unify(valueType)) {
            throw new TypeException("Type mismatch: " + targetType + " != " + valueType);
        }
        generify(targetType);
        result = assign(target, value);
    }

    @Override
    public void visitBlock(Block node) {
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
    public void visitBooleanLiteral(BooleanLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitBreakExpression(BreakExpression node) {
        result = Break.INSTANCE;
    }

    @Override
    public void visitCharacterLiteral(CharacterLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitConditionCase(ConditionCase node) {
        result = guard(translate(node.getCondition()), translate(node.getExpression()));
    }

    @Override
    public void visitConditional(Conditional node) {
        List<AstNode> cases = new ArrayList<>();
        for (Symbol n : node.getCases()) {
            translate(n);
            cases.add(result);
        }
        result = guards(cases);
    }

    @Override
    public void visitContinueExpression(ContinueExpression node) {
        result = Continue.INSTANCE;
    }

    @Override
    public void visitDeclaration(Declaration node) {
        typeErrors.clear();
        reserveName(node.getName());
        AstNode body = translate(node.getBody());
        if (!body.isInvokable()) {
            body = expression(body);
        }
        DeclaredExpression declaration = declaration(getModule(), node.getName(), body);
        if (declaration.getType().decompose().isEmpty()) {
            throw new TypeException(join(typeErrors, "; "));
        }
        register(declaration.getName(), declaration.getType());
        declarations.add(declaration);
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitEmbraceCase(EmbraceCase node) {
        define(locator(node.getArgument()), Type.var("E"));
        result = embrace(node.getArgument(), javaClass(node.getType()), translate(node.getExpression()));
    }

    @Override
    public void visitExceptional(ExceptionalExpression node) {
        enterScope();
        AstNode begin = begin(translate(node.getExpression()));
        List<AstNode> embraces = new ArrayList<>();
        AstNode ensure = null;
        if (node.getEnsureCase() != null) {
            ensure = translate(node.getEnsureCase());
            matchTypes(begin, ensure);
        }
        for (Symbol symbol : node.getEmbraceCases()) {
            AstNode embrace = translate(symbol);
            matchTypes(begin, embrace);
            embraces.add(embrace);
        }
        leaveScope();
        result = exceptional(begin, embraces, ensure);
    }

    @Override
    public void visitFromImport(FromImport node) {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        for (Symbol s : node.getSubImports()) {
            SubImport sub = (SubImport) s;
            translate(importId(qid(identifier, sub.getExpression()), sub.getAlias()));
        }
    }

    @Override
    public void visitFunctionLiteral(FunctionLiteral node) {
        AstNode function = translateFunction(node);
        validateResultType(node, function);
        result = function;
    }

    @Override
    public void visitHurl(HurlExpression node) {
        result = hurl(translate(node.getExpression()));
    }

    @Override
    public void visitIdentifier(Identifier node) {
        result = reference(node.getValue());
    }

    @Override
    public void visitImport(Import node) {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        List<String> segments = identifier.getSegments();
        Locator locator = locator(join(segments.subList(0, segments.size() - 1), '.'), identifier.getLastSegment());
        addAlias(node.getAlias(), locator);
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitInvocation(Invocation node) {
        result = invoke(translate(node.getExpression()));
    }

    @Override
    public void visitInvokableLiteral(InvokableLiteral node) {
        beginFunction();
        AstNode invokable = invokable(translate(node.getExpression()));
        leaveFunction();
        if (result(invokable.getType()).decompose().isEmpty()) {
            throw new TypeException("Could not determine type of invokable");
        }
        result = invokable;
    }

    @Override
    public void visitIsExpression(IsExpression node) {
        result = is(translate(node.getLeft()), translate(node.getRight()));
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitListLiteral(ListLiteral node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitLoopExpression(LoopExpression node) {
        result = loop(
            translate(node.getCondition()),
            translate(node.getBody())
        );
    }

    @Override
    public void visitMapEntry(MapEntry node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitMapLiteral(MapLiteral node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitModule(Module node) {
        for (Symbol element : node.getElements()) {
            translate(element);
        }
    }

    @Override
    public void visitNopExpression(NopExpression node) {
        result = nop();
    }

    @Override
    public void visitNothingLiteral(NothingLiteral node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitQualifiedIdentifier(QualifiedIdentifier node) {
        List<String> segments = node.getSegments();
        if (segments.size() > 1) {
            throw new UnsupportedOperationException(); // TODO
        }
        result = reference(segments.get(0));
    }

    @Override
    public void visitRegexLiteral(RegexLiteral node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitResult(Result node) {
        result = result(translate(node.getExpression()));
    }

    @Override
    public void visitSetLiteral(SetLiteral node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitStringLiteral(StringLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitSubImport(SubImport node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitSymbolLiteral(SymbolLiteral node) {
        result = symbol(node.getValue());
    }

    @Override
    public void visitTupleLiteral(TupleLiteral node) {
        List<AstNode> elements = new ArrayList<>();
        for (Symbol element : node.getElements()) {
            elements.add(translate(element));
        }
        result = tuple(elements);
    }

    @Override
    public void visitTypeSpec(TypeSpec node) {
        result = translate(node.getType());
    }

    @Override
    public void visitUsing(Using node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitVar(Var node) {
        AstNode value = translate(node.getValue());
        Type varType = createVariable();
        define(locator(node.getName()), varType);
        specialize(varType);
        varType.unify(value.getType());
        generify(varType);
        result = var(node.getName(), value);
    }

    @Override
    public void visitVarDeclaration(VarDeclaration node) {
        Type varType = createVariable();
        define(locator(node.getName()), varType);
        specialize(varType);
        result = new VariableDeclaration(node.getName());
    }

    @Override
    public void visitWildcardImport(WildcardImport node) {
        addWildcardImport(join(((QualifiedIdentifier) node.getModule()).getSegments(), '.'));
    }

    private void beginFunction() {
        functionLevel++;
    }

    private Locator findWildcard(String value) {
        for (String module : wildcardImports) {
            if (environment().isDefined(locator(module, value))) {
                return locator(module, value);
            }
        }
        throw new UndefinedSymbolException("Symbol '" + value + "' is undefined");
    }

    private String generateName() {
        return currentName.generateName();
    }

    private Type inferenceFunctionType(FunctionLiteral functionNode, DeclaredArgument argument) {
        List<Type> allowedTypes = new ArrayList<>();
        for (Type argumentSubType : argument.getType().decompose()) {
            enterScope();
            define(argument.getLocator(), argumentSubType);
            specialize(argument.getType());
            Type bodyType = translate(functionNode.getBody()).getType();
            leaveScope();
            for (Type bodySubType : bodyType.decompose()) {
                allowedTypes.add(func(argumentSubType, bodySubType));
            }
        }
        return set(allowedTypes);
    }

    private Type inferenceResultType(Type functionType, Type argumentType) {
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
            if (functionTypesQueue.isEmpty()) {
                break;
            } else {
                functionTypesQueue.remove(0);
            }
        }
        if (allowedTypes.isEmpty()) {
            typeErrors.add("Could not apply function " + functionType + " to argument " + argumentType);
        }
        argumentType.bind(set(constrainedArgumentType.decompose()));
        return set(allowedTypes);
    }

    private String javaClass(Symbol symbol) {
        TypeSpec type = (TypeSpec) symbol;
        QualifiedIdentifier id = (QualifiedIdentifier) type.getType();
        return join(id.getSegments(), ".");
    }

    private void leaveFunction() {
        functionLevel--;
    }

    private void matchTypes(AstNode left, AstNode right) {
        if (!left.getType().unify(right.getType())) {
            throw new TypeException("Type mismatch: " + left.getType() + " != " + right.getType());
        }
    }

    private void reserveName(String name) {
        currentName = new NameSequence(name);
    }

    private AstNode translate(Visitable node) {
        node.accept(this);
        return result;
    }

    private AstNode translate(Symbol node) {
        return translate((Visitable) node);
    }

    private AstNode translateFunction(FunctionLiteral node) {
        beginFunction();
        enterScope();
        DeclaredArgument argument = (DeclaredArgument) translate(node.getArgument());
        String name = generateName();
        AstNode body = translate(node.getBody());
        leaveScope();
        Type functionType = inferenceFunctionType(node, argument);
        if (functionLevel > 1) {
            leaveFunction();
            Locator locator = names.get(node);
            if (locator == null) {
                Collection<String> environment = environment().getVariables();
                register(name, functionType);
                declarations.add(declaration(module, name, closure(argument.getName(), environment, body, functionType)));
                locator = new ClosureLocator(module, name, environment);
                names.put(node, locator);
            }
            register(locator.getName(), functionType);
            return new Reference(locator, functionType);
        } else {
            return func(argument.getName(), body, functionType);
        }
    }

    private Type translateType(Symbol node) {
        if (node == null) {
            return createVariable();
        } else {
            return translate(node).getType();
        }
    }

    private boolean unifyFunctionResult(Type functionType, Type declaredResultType) {
        if (functionType.decompose().size() == 1) {
            Type actualResultType = result(functionType);
            return actualResultType.unify(declaredResultType);
        } else {
            return false;
        }
    }

    private void validateResultType(FunctionLiteral node, AstNode function) {
        if (node.getType() != null) {
            Type resultType = translateType(node.getType());
            if (!unifyFunctionResult(function.getType(), resultType)) {
                throw new TypeException(
                    "Function type " + function.getType() + " not compatible with declared result type " + resultType
                );
            }
        }
    }

    private static final class NameSequence {

        private final String name;
        private int nextId;

        public NameSequence(String name) {
            this.name = name;
        }

        public String generateName() {
            return name + "_" + nextId++;
        }

        public String getName() {
            return name;
        }
    }
}
