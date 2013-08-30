package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.compiler.AstFactory.*;
import static snacks.lang.compiler.SyntaxFactory.importId;
import static snacks.lang.compiler.SyntaxFactory.qid;
import static snacks.lang.compiler.ast.Type.*;

import java.util.*;
import beaver.Symbol;
import snacks.lang.compiler.ast.*;
import snacks.lang.compiler.syntax.*;
import snacks.lang.compiler.syntax.Result;

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
        this.wildcardImports = new ArrayList<>(asList("snacks/lang"));
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
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitAnnotated(Annotated node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitAnnotation(Annotation node) {
        throw new UnsupportedOperationException(); // TODO
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
    public void visitCharacterLiteral(CharacterLiteral node) {
        throw new UnsupportedOperationException(); // TODO
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
    public void visitDefaultCase(DefaultCase node) {
        result = translate(node.getExpression());
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitEmbraceCase(EmbraceCase node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitEnsureCase(EnsureCase node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitExceptional(Exceptional node) {
        throw new UnsupportedOperationException(); // TODO
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
    public void visitHurl(Hurl node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitIdentifier(Identifier node) {
        result = reference(node.getValue());
    }

    @Override
    public void visitImport(Import node) {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        List<String> segments = identifier.getSegments();
        Locator locator = locator(join(segments.subList(0, segments.size() - 1), '/'), identifier.getLastSegment());
        addAlias(node.getAlias(), locator);
    }

    @Override
    public void visitIndexExpression(IndexExpression node) {
        throw new UnsupportedOperationException(); // TODO
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
    public void visitIsExpression(IsExpression node) {
        result = is(translate(node.getLeft()), translate(node.getRight()));
    }

    @Override
    public void visitInvokableLiteral(InvokableLiteral node) {
        beginFunction();
        result = invokable(translate(node.getExpression()));
        leaveFunction();
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
    public void visitLoop(Loop node) {
        throw new UnsupportedOperationException(); // TODO
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
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitTupleLiteral(TupleLiteral node) {
        throw new UnsupportedOperationException(); // TODO
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
        Type defVarType = createVariable();
        define(locator(node.getName()), defVarType);
        specialize(defVarType);
        Type defActualType = value.getType();
        defVarType.unify(defActualType);
        generify(defVarType);
        result = var(node.getName(), value);
    }

    @Override
    public void visitWildcardImport(WildcardImport node) {
        addWildcardImport(join(((QualifiedIdentifier) node.getModule()).getSegments(), '/'));
    }

    private AstNode acceptFunction(FunctionLiteral node, DeclaredArgument argument, AstNode body, Type functionType) {
        if (functionLevel == 1) {
            return func(argument.getName(), body, functionType);
        } else {
            leaveFunction();
            Locator locator = names.get(node);
            if (locator == null) {
                String name = generateName();
                Collection<String> environment = environment().getVariables();
                register(name, functionType);
                declarations.add(declaration(module, name, closure(argument.getName(), environment, body, functionType)));
                locator = new ClosureLocator(module, name, environment);
                names.put(node, locator);
            }
            register(locator.getName(), functionType);
            return new Reference(locator, functionType);
        }
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

    private void leaveFunction() {
        functionLevel--;
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
        AstNode body = translate(node.getBody());
        leaveScope();
        Type functionType = inferenceFunctionType(node, argument);
        return acceptFunction(node, argument, body, functionType);
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
