package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.ast.AstFactory.*;
import static snacks.lang.ast.AstFactory.func;
import static snacks.lang.ast.AstFactory.record;
import static snacks.lang.ast.AstFactory.tuple;
import static snacks.lang.parser.syntax.SyntaxFactory.id;
import static snacks.lang.parser.syntax.SyntaxFactory.importId;
import static snacks.lang.parser.syntax.SyntaxFactory.qid;
import static snacks.lang.parser.syntax.SyntaxFactory.type;
import static snacks.lang.type.Types.*;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.record;
import static snacks.lang.type.Types.tuple;

import java.util.*;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Operator;
import snacks.lang.SnackKind;
import snacks.lang.ast.*;
import snacks.lang.parser.syntax.*;
import snacks.lang.parser.syntax.Result;
import snacks.lang.type.FunctionType;
import snacks.lang.type.RecordType;
import snacks.lang.type.RecordType.Property;
import snacks.lang.type.Type;
import snacks.lang.type.Types;

public class Translator implements SyntaxVisitor {

    private final String module;
    private final Deque<SymbolEnvironment> environments;
    private final List<NamedNode> declarations;
    private final Map<String, AliasEntry> aliases;
    private final List<String> wildcardImports;
    private final List<String> typeErrors;
    private final Map<Symbol, Locator> names;
    private AstNode result;
    private Type type;
    private int functionLevel;
    private NameSequence currentName;

    public Translator(SymbolEnvironment environment, String module) {
        this.module = module;
        this.environments = new ArrayDeque<>(asList(environment));
        this.declarations = new ArrayList<>();
        this.aliases = new HashMap<>();
        this.typeErrors = new ArrayList<>();
        this.names = new IdentityHashMap<>();
        this.wildcardImports = new ArrayList<>(asList("snacks.lang"));
    }

    public List<NamedNode> translateModule(Symbol node) {
        translate(node);
        return new ArrayList<>(declarations);
    }

    @Override
    public void visitAccessExpression(AccessExpression node) {
        AstNode expression = translate(node.getExpression());
        String property = node.getProperty();
        for (Type type : expression.getType().decompose()) {
            if (type instanceof RecordType) {
                for (Property propertyType : ((RecordType) type).getProperties()) {
                    if (propertyType.getName().equals(property)) {
                        result = access(expression, property, propertyType.getType());
                        return;
                    }
                }
            }
        }
        throw new TypeException("Could not locate property '" + property + "' on type " + expression.getType());
    }

    @Override
    public void visitAndExpression(AndExpression node) {
        AstNode left = translate(node.getLeft());
        AstNode right = translate(node.getRight());
        if (!left.getType().unify(BOOLEAN_TYPE)) {
            throw new TypeException("Left-hand side of LOGICAL AND is not a boolean: got " + left.getType());
        } else if (!right.getType().unify(BOOLEAN_TYPE)) {
            throw new TypeException("Right-hand side of LOGICAL AND is not a boolean: got " + right.getType());
        } else {
            result = new LogicalAnd(left, right);
        }
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
        verifyAssignmentType(target.getType(), value.getType());
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
    public void visitConstructorExpression(ConstructorExpression node) {
        List<AstNode> arguments = new ArrayList<>();
        for (Symbol argument : node.getArguments()) {
            arguments.add(translate(argument));
        }
        result = initializer(translate(node.getConstructor()), arguments);
    }

    @Override
    public void visitContinueExpression(ContinueExpression node) {
        result = Continue.INSTANCE;
    }

    @Override
    public void visitDoubleLiteral(DoubleLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitEmbraceCase(EmbraceCase node) {
        define(locator(node.getArgument()), Types.var("E"));
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
    public void visitExpressionDeclaration(ExpressionDeclaration node) {
        declarations.add(namedDeclaration(node.getName(), node.getBody()));
    }

    private DeclaredExpression namedDeclaration(String name, Symbol expression) {
        functionLevel = 0;
        if (expression instanceof InvokableLiteral) {
            functionLevel++; // TODO hack
        }
        typeErrors.clear();
        reserveName(name);
        AstNode body = translate(expression);
        if (!body.isInvokable()) {
            body = expression(body);
        }
        Locator locator = locator(module, name);
        DeclaredExpression declaration = declaration(module, name, body);
        if (declaration.getType().decompose().isEmpty()) {
            throw new TypeException(join(typeErrors, "; "));
        }
        if (environment().hasSignature(locator)) {
            Type type = environment().getSignature(locator);
            if (declaration.getType().unify(type)) {
                declaration.setType(type);
            } else {
                throw new TypeException("Type mismatch: " + declaration.getType() + " != " + environment().typeOf(locator));
            }
        }
        if (declarations.contains(declaration)) {
            throw new UndefinedSymbolException("Cannot redefine " + declaration.getName());
        }
        if (environment().isOperator(declaration.getName())) {
            declaration.setOperator(environment().getOperator(declaration.getName()));
        }
        addAlias(name, module, name);
        environment().define(new Reference(locator, declaration.getType()));
        return declaration;
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
    public void visitFunctionSignature(FunctionSignature node) {
        type = func(translateType(node.getArgument()), translateType(node.getResult()));
    }

    @Override
    public void visitHurl(HurlExpression node) {
        result = hurl(translate(node.getExpression()));
    }

    @Override
    public void visitIdentifier(Identifier node) {
        result = reference(node.getName(), EXPRESSION);
    }

    @Override
    public void visitImport(Import node) {
        QualifiedIdentifier identifier = (QualifiedIdentifier) node.getModule();
        List<String> segments = identifier.getSegments();
        addAlias(node.getAlias(), join(segments.subList(0, segments.size() - 1), '.'), identifier.getLastSegment());
    }

    @Override
    public void visitInitializerExpression(InitializerExpression node) {
        AstNode constructor = translate(node.getConstructor());
        Type type = constructor.getType();
        if (type instanceof RecordType) {
            throw new UnsupportedOperationException(); // TODO implement record copy syntax
        } else if (type instanceof FunctionType) {
            while (!(type instanceof RecordType)) {
                type = resultOf(type);
            }
            RecordType recordType = (RecordType) type; // TODO hack
            Map<String, PropertyInitializer> properties = new HashMap<>();
            List<Property> matchedProperties = new ArrayList<>();
            for (Symbol n : node.getProperties()) {
                PropertyInitializer property = (PropertyInitializer) translate(n);
                if (properties.containsKey(property.getName())) {
                    throw new DuplicatePropertyException("Duplicate property initializer: " + property.getName());
                }
                boolean found = false;
                for (Property propertyType : recordType.getProperties()) {
                    if (propertyType.getName().equals(property.getName())) {
                        verifyAssignmentType(propertyType.getType(), property.getType());
                        matchedProperties.add(propertyType);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new TypeException("Property " + recordType.getName() + "#" + property.getName() + " does not exist");
                }
                properties.put(property.getName(), property);
            }
            List<Property> propertyTypes = recordType.getProperties();
            for (int i = propertyTypes.size() - 1; i >= 0; i--) {
                if (matchedProperties.contains(propertyTypes.get(i))) {
                    Type resultType = null;
                    if (i == 0) {
                        resultType = type;
                    }
                    constructor = apply(constructor, properties.get(propertyTypes.get(i).getName()).getValue(), resultType);
                } else {
                    throw new MissingPropertyException("Missing property: " + propertyTypes.get(i));
                }
            }
            result = constructor;
        }
    }

    @Override
    public void visitIntegerLiteral(IntegerLiteral node) {
        result = constant(node.getValue());
    }

    @Override
    public void visitInvokableLiteral(InvokableLiteral node) {
        beginFunction();
        enterScope();
        AstNode body = translate(node.getBody());
        if (body.getType().decompose().isEmpty()) {
            throw new TypeException("Unable to determine type of invokable");
        }
        Type functionType = func(VOID_TYPE, body.getType());
        leaveScope();
        leaveFunction();
        if (functionLevel > 1) {
            Locator locator = names.get(node);
            if (locator == null) {
                String name = generateName();
                Collection<String> environment = environment().getVariables();
                register(name, functionType);
                declarations.add(declaration(module, name, closure(environment, body)));
                locator = new ClosureLocator(module, name, environment);
                names.put(node, locator);
            }
            register(locator.getName(), functionType);
            result = new Reference(locator, functionType);
        } else {
            result = invokable(body);
        }
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
    public void visitMessage(Message node) {
        result = translate(shuffleMessage(node));
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
    public void visitOperatorDeclaration(OperatorDeclaration node) {
        Operator operator = node.getOperator();
        if (operator.isPrefix()) {
            environment().registerPrefix(operator.getPrecedence(), operator.getName());
        } else {
            environment().registerInfix(operator.getPrecedence(), operator.getFixity(), operator.getName());
        }
    }

    @Override
    public void visitOrExpression(OrExpression node) {
        AstNode left = translate(node.getLeft());
        AstNode right = translate(node.getRight());
        if (!left.getType().unify(BOOLEAN_TYPE)) {
            throw new TypeException("Left-hand side of LOGICAL OR is not a boolean: got " + left.getType());
        } else if (!right.getType().unify(BOOLEAN_TYPE)) {
            throw new TypeException("Right-hand side of LOGICAL OR is not a boolean: got " + right.getType());
        } else {
            result = new LogicalOr(left, right);
        }
    }

    @Override
    public void visitPropertyDeclaration(PropertyDeclaration node) {
        result = propDef(node.getName(), translateType(node.getType()));
    }

    @Override
    public void visitPropertyExpression(PropertyExpression node) {
        result = prop(node.getName(), translate(node.getValue()));
    }

    @Override
    public void visitQualifiedIdentifier(QualifiedIdentifier node) {
        result = referenceQualifiedIdentifier(node, EXPRESSION);
    }

    @Override
    public void visitQuotedIdentifier(QuotedIdentifier node) {
        result = reference(node.getName(), EXPRESSION);
    }

    @Override
    public void visitRecordDeclaration(RecordDeclaration node) {
        List<Property> properties = new ArrayList<>();
        for (Symbol property : node.getProperties()) {
            DeclaredProperty p = (DeclaredProperty) translate(property);
            properties.add(property(p.getName(), p.getType()));
        }
        define(locator(module, node.getName(), TYPE), record(module + '.' + node.getName(), properties));
        addAlias(node.getName(), module, node.getName());
        defineConstructor(node.getName(), properties);
        result = record(module, node.getName(), properties);
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
    public void visitSignature(Signature node) {
        Reference reference = new Reference(locator(module, node.getIdentifier()), translateType(node.getType()));
        addAlias(reference.getLocator().getName(), module, reference.getLocator().getName());
        environment().signature(reference);
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
    public void visitTupleSignature(TupleSignature node) {
        List<Type> types = new ArrayList<>();
        for (Symbol type : node.getTypes()) {
            types.add(translateType(type));
        }
        type = tuple(types);
    }

    @Override
    public void visitTypeDeclaration(TypeDeclaration node) {
        Locator locator = locator(module, node.getName(), TYPE);
        DeclaredType declaration = new DeclaredType(module, node.getName());
        addAlias(node.getName(), module, node.getName());
        environment().define(new Reference(locator, declaration.getType()));
        for (Symbol variant : node.getVariants()) {
            declaration.addVariant(translate(variant));
        }
        declarations.add(declaration);
    }

    @Override
    public void visitTypeSpec(TypeSpec node) {
        result = referenceQualifiedIdentifier(node.getName(), TYPE);
    }

    @Override
    public void visitTypeVariable(TypeVariable node) {
        type = environment().createVariable();
    }

    @Override
    public void visitUnitLiteral(UnitLiteral node) {
        result = unit();
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
        result = AstFactory.var(node.getName(), value);
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

    private void addAlias(String alias, String module, String name) {
        aliases.put(alias, new AliasEntry(module, name));
    }

    private void addWildcardImport(String wildcardImport) {
        wildcardImports.add(wildcardImport);
    }

    private void beginFunction() {
        functionLevel++;
    }

    private Type createVariable() {
        return environment().createVariable();
    }

    private void define(Locator locator, Type type) {
        environment().define(new Reference(locator, type));
    }

    private void defineConstructor(String name, List<Property> properties) {
        Type constructorType = record(module + '.' + name, properties);
        for (int i = properties.size() - 1; i >= 0; i--) {
            constructorType = func(properties.get(i).getType(), constructorType);
        }
        define(locator(module, name, EXPRESSION), constructorType);
        Symbol constructor = type(qid(module, name));
        List<Symbol> arguments = new ArrayList<>();
        for (Property property : properties) {
            arguments.add(id(property.getName()));
        }
        constructor = new ConstructorExpression(constructor, arguments);
        for (int i = 0; i < properties.size(); i++) {
            constructor = new FunctionLiteral(
                new Argument(properties.get(i).getName(), type(properties.get(i).getType().getName().split("\\."))),
                constructor,
                null
            );
        }
        declarations.add(new DeclaredConstructor(module, name, namedDeclaration(name + "Constructor", constructor).getBody()));
    }

    private void enterScope() {
        environments.push(environment().extend());
    }

    private SymbolEnvironment environment() {
        return environments.peek();
    }

    private Locator findWildcard(String value, SnackKind kind) {
        for (String module : wildcardImports) {
            if (environment().isDefined(locator(module, value, kind))) {
                return locator(module, value, kind);
            }
        }
        throw new UndefinedSymbolException("Symbol '" + value + "' is undefined");
    }

    private String generateName() {
        return currentName.generateName();
    }

    private void generify(Type type) {
        environment().generify(type);
    }

    private Operator getOperator(Symbol node) {
        return environment().getOperator(((Identifier) node).getName());
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
        return union(allowedTypes);
    }

    private Type inferenceResultType(Type functionType, Type argumentType) {
        Set<Type> argumentTypes = new HashSet<>();
        Set<Type> resultTypes = new HashSet<>();
        for (FunctionArgument pair : productOf(functionType, argumentType)) {
            Type resultType = createVariable();
            if (func(pair.argumentType, resultType).unify(pair.functionType)) {
                resultTypes.add(resultType);
                argumentTypes.add(pair.argumentType);
            }
        }
        if (resultTypes.isEmpty()) {
            typeErrors.add("Could not apply function " + functionType + " to argument " + argumentType);
        }
        argumentType.bind(union(argumentTypes));
        return union(resultTypes);
    }

    private boolean isOperator(Symbol node) {
        if (node instanceof Identifier) {
            String name = ((Identifier) node).getName();
            if (!"=".equals(name)) {
                reference(name, EXPRESSION);
            }
            return environment().isOperator(name);
        } else {
            return false;
        }
    }

    private boolean isPrefix(Symbol symbol) {
        return getOperator(symbol).isPrefix();
    }

    private String javaClass(Symbol symbol) {
        TypeSpec type = (TypeSpec) symbol;
        QualifiedIdentifier id = type.getName();
        return join(id.getSegments(), ".");
    }

    private void leaveFunction() {
        functionLevel--;
    }

    private void leaveScope() {
        environments.pop();
    }

    private void matchTypes(AstNode left, AstNode right) {
        if (!left.getType().unify(right.getType())) {
            throw new TypeException("Type mismatch: " + left.getType() + " != " + right.getType());
        }
    }

    private boolean outputOperator(Operator o1, Operator o2) {
        return o1.getFixity() == LEFT && o1.getPrecedence() <= o2.getPrecedence()
            || (o1.isPrefix() || o2.isPrefix()) && o1.getPrecedence() == o2.getPrecedence();
    }

    private Set<FunctionArgument> productOf(Type functionType, Type argumentType) {
        Set<FunctionArgument> product = new HashSet<>();
        for (Type ftype : functionType.decompose()) {
            for (Type atype : argumentType.recompose(ftype, environment()).decompose()) {
                product.add(new FunctionArgument(ftype, atype));
            }
        }
        return product;
    }

    private Symbol reduceOperations(Deque<Symbol> output) {
        Deque<Symbol> stack = new ArrayDeque<>();
        while (!output.isEmpty()) {
            Symbol op = output.pollLast();
            if (isOperator(op)) {
                if (isPrefix(op)) {
                    stack.push(new ApplyExpression(op, stack.pop()));
                } else {
                    Symbol right = stack.pop();
                    Symbol left = stack.pop();
                    Symbol symbol;
                    switch (getOperator(op).getName()) {
                        case "and": symbol = new AndExpression(left, right); break;
                        case "or": symbol = new OrExpression(left, right); break;
                        default: symbol = new ApplyExpression(new ApplyExpression(op, left), right);
                    }
                    stack.push(symbol);
                }
            } else {
                stack.push(op);
            }
        }
        return stack.pop();
    }

    private Reference reference(String value, SnackKind kind) {
        Locator locator = new VariableLocator(value);
        if (!environment().isDefined(locator)) {
            if (aliases.containsKey(value)) {
                AliasEntry entry = aliases.get(value);
                locator = locator(entry.getModule(), entry.getName(), kind);
            } else {
                locator = findWildcard(value, kind);
            }
        }
        return new Reference(locator, environment().typeOf(locator));
    }

    private AstNode referenceQualifiedIdentifier(QualifiedIdentifier node, SnackKind kind) {
        List<String> segments = node.getSegments();
        if (segments.size() > 1) {
            String module = join(segments.subList(0, segments.size() - 1), '.');
            String name = segments.get(segments.size() - 1);
            Locator locator = locator(module, name, kind);
            if (environment().isDefined(locator)) {
                return new Reference(locator, environment().typeOf(locator));
            } else {
                throw new UndefinedSymbolException("Symbol " + join(segments, '.') + " is not defined");
            }
        } else {
            return reference(segments.get(0), kind);
        }
    }

    private void register(String name, Type type) {
        Locator locator = locator(module, name);
        addAlias(name, module, name);
        environment().define(new Reference(locator, type));
    }

    private void reserveName(String name) {
        currentName = new NameSequence(name);
    }

    private Symbol shuffleMessage(Message message) {
        Deque<Symbol> input = new ArrayDeque<>(message.getElements());
        Deque<Symbol> output = new ArrayDeque<>();
        Deque<Operator> operators = new ArrayDeque<>();
        boolean expectPrefix = isOperator(input.peek());
        while (!input.isEmpty()) {
            if (isOperator(input.peek())) {
                Operator o1 = getOperator(input.poll());
                if (expectPrefix) {
                    o1 = toPrefix(o1);
                }
                if (!operators.isEmpty()) {
                    Operator o2 = operators.peek();
                    while (!operators.isEmpty() && outputOperator(o1, o2)) {
                        output.push(new Identifier(operators.pop().getName()));
                        o2 = operators.peek();
                    }
                }
                operators.push(o1);
                expectPrefix = isOperator(input.peek());
            } else {
                output.push(input.poll());
                while (!input.isEmpty() && !isOperator(input.peek())) {
                    output.push(new ApplyExpression(output.pop(), input.poll()));
                }
            }
        }
        while (!operators.isEmpty()) {
            output.push(new Identifier(operators.pop().getName()));
        }
        return reduceOperations(output);
    }

    private void specialize(Type type) {
        environment().specialize(type);
    }

    private Operator toPrefix(Operator op) {
        switch (op.getName()) {
            case "-": op = op.toPrefix("unary-"); break;
            case "+": op = op.toPrefix("unary+"); break;
            case "~": op = op.toPrefix("unary~"); break;
            case "!": op = op.toPrefix("not"); break;
            default:
                if (!op.isPrefix()) {
                    throw new ParseException("Unexpected binary operator: " + op);
                }
        }
        return op;
    }

    private AstNode translate(VisitableSymbol node) {
        type = null;
        result = null;
        node.accept(this);
        return result;
    }

    private AstNode translate(Symbol node) {
        return translate((VisitableSymbol) node);
    }

    private AstNode translateFunction(FunctionLiteral node) {
        beginFunction();
        enterScope();
        DeclaredArgument argument = (DeclaredArgument) translate(node.getArgument());
        AstNode body = translate(node.getBody());
        leaveScope();
        Type functionType = inferenceFunctionType(node, argument);
        if (functionLevel > 1) {
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
        } else {
            return func(argument.getName(), body, functionType);
        }
    }

    private Type translateType(Symbol node) {
        if (node == null) {
            return createVariable();
        } else {
            translate(node);
            if (type == null) {
                return result.getType();
            } else {
                return type;
            }
        }
    }

    private boolean unifyFunctionResult(Type functionType, Type declaredResultType) {
        if (functionType.decompose().size() == 1) {
            Type actualResultType = resultOf(functionType);
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

    private void verifyAssignmentType(Type targetType, Type valueType) {
        specialize(targetType);
        if (!targetType.unify(valueType)) {
            throw new TypeException("Type mismatch: " + targetType + " != " + valueType);
        }
        generify(targetType);
    }

    private static final class AliasEntry {

        private final String module;
        private final String name;

        private AliasEntry(String module, String name) {
            this.module = module;
            this.name = name;
        }

        private String getModule() {
            return module;
        }

        private String getName() {
            return name;
        }
    }

    private static final class FunctionArgument {

        final Type argumentType;
        final Type functionType;

        public FunctionArgument(Type functionType, Type argumentType) {
            this.argumentType = argumentType;
            this.functionType = functionType;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof FunctionArgument) {
                FunctionArgument other = (FunctionArgument) o;
                return new EqualsBuilder()
                    .append(argumentType, other.argumentType)
                    .append(functionType, other.functionType)
                    .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(argumentType, functionType);
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
    }
}
