package snacks.lang.parser;

import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Operator;
import snacks.lang.SnackKind;
import snacks.lang.SnacksList;
import snacks.lang.Type;
import snacks.lang.Type.AlgebraicType;
import snacks.lang.Type.FunctionType;
import snacks.lang.Type.RecordType;
import snacks.lang.Type.RecordType.Property;
import snacks.lang.Type.VariableType;
import snacks.lang.TypeUnroller;
import snacks.lang.Types;
import snacks.lang.ast.AstFactory;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Break;
import snacks.lang.ast.ClosureLocator;
import snacks.lang.ast.Continue;
import snacks.lang.ast.DeclarationLocator;
import snacks.lang.ast.DeclaredArgument;
import snacks.lang.ast.DeclaredConstructor;
import snacks.lang.ast.DeclaredExpression;
import snacks.lang.ast.DeclaredProperty;
import snacks.lang.ast.DeclaredRecord;
import snacks.lang.ast.DeclaredType;
import snacks.lang.ast.Locator;
import snacks.lang.ast.LogicalAnd;
import snacks.lang.ast.LogicalOr;
import snacks.lang.ast.NamedNode;
import snacks.lang.ast.PatternCase;
import snacks.lang.ast.PropertyInitializer;
import snacks.lang.ast.Reference;
import snacks.lang.ast.UndefinedSymbolException;
import snacks.lang.ast.VariableDeclaration;
import snacks.lang.ast.VariableLocator;
import snacks.lang.parser.syntax.AccessExpression;
import snacks.lang.parser.syntax.AndExpression;
import snacks.lang.parser.syntax.AnyMatcher;
import snacks.lang.parser.syntax.ApplyExpression;
import snacks.lang.parser.syntax.Argument;
import snacks.lang.parser.syntax.AssignmentExpression;
import snacks.lang.parser.syntax.Block;
import snacks.lang.parser.syntax.BooleanLiteral;
import snacks.lang.parser.syntax.BreakExpression;
import snacks.lang.parser.syntax.CaptureMatcher;
import snacks.lang.parser.syntax.CharacterLiteral;
import snacks.lang.parser.syntax.ConditionCase;
import snacks.lang.parser.syntax.Conditional;
import snacks.lang.parser.syntax.ConstantDeclaration;
import snacks.lang.parser.syntax.ConstantMatcher;
import snacks.lang.parser.syntax.ConstructorExpression;
import snacks.lang.parser.syntax.ConstructorMatcher;
import snacks.lang.parser.syntax.ContinueExpression;
import snacks.lang.parser.syntax.DoubleLiteral;
import snacks.lang.parser.syntax.EmbraceCase;
import snacks.lang.parser.syntax.ExceptionalExpression;
import snacks.lang.parser.syntax.ExpressionDeclaration;
import snacks.lang.parser.syntax.FromImport;
import snacks.lang.parser.syntax.FunctionLiteral;
import snacks.lang.parser.syntax.FunctionSignature;
import snacks.lang.parser.syntax.HurlExpression;
import snacks.lang.parser.syntax.Identifier;
import snacks.lang.parser.syntax.Import;
import snacks.lang.parser.syntax.InitializerExpression;
import snacks.lang.parser.syntax.IntegerLiteral;
import snacks.lang.parser.syntax.InvokableLiteral;
import snacks.lang.parser.syntax.IteratorLoop;
import snacks.lang.parser.syntax.LoopExpression;
import snacks.lang.parser.syntax.MapEntry;
import snacks.lang.parser.syntax.MapLiteral;
import snacks.lang.parser.syntax.Message;
import snacks.lang.parser.syntax.Module;
import snacks.lang.parser.syntax.NamedPattern;
import snacks.lang.parser.syntax.NopExpression;
import snacks.lang.parser.syntax.OperatorDeclaration;
import snacks.lang.parser.syntax.OrExpression;
import snacks.lang.parser.syntax.PatternMatcher;
import snacks.lang.parser.syntax.PropertyDeclaration;
import snacks.lang.parser.syntax.PropertyExpression;
import snacks.lang.parser.syntax.PropertyMatcher;
import snacks.lang.parser.syntax.ProtocolDeclaration;
import snacks.lang.parser.syntax.ProtocolImplementation;
import snacks.lang.parser.syntax.QualifiedIdentifier;
import snacks.lang.parser.syntax.QuotedIdentifier;
import snacks.lang.parser.syntax.QuotedOperator;
import snacks.lang.parser.syntax.RecordDeclaration;
import snacks.lang.parser.syntax.RecordMatcher;
import snacks.lang.parser.syntax.RegexLiteral;
import snacks.lang.parser.syntax.Result;
import snacks.lang.parser.syntax.SetLiteral;
import snacks.lang.parser.syntax.Signature;
import snacks.lang.parser.syntax.StringLiteral;
import snacks.lang.parser.syntax.SubImport;
import snacks.lang.parser.syntax.SymbolLiteral;
import snacks.lang.parser.syntax.SyntaxVisitor;
import snacks.lang.parser.syntax.TupleLiteral;
import snacks.lang.parser.syntax.TupleSignature;
import snacks.lang.parser.syntax.TypeDeclaration;
import snacks.lang.parser.syntax.TypeReference;
import snacks.lang.parser.syntax.TypeSpec;
import snacks.lang.parser.syntax.TypeVariable;
import snacks.lang.parser.syntax.UnitLiteral;
import snacks.lang.parser.syntax.Using;
import snacks.lang.parser.syntax.Var;
import snacks.lang.parser.syntax.VarDeclaration;
import snacks.lang.parser.syntax.VisitableSymbol;
import snacks.lang.parser.syntax.WildcardImport;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.SnacksList.fromList;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.property;
import static snacks.lang.Types.recur;
import static snacks.lang.Types.resultOf;
import static snacks.lang.Types.tuple;
import static snacks.lang.Types.union;
import static snacks.lang.Types.var;
import static snacks.lang.Types.voidType;
import static snacks.lang.ast.AstFactory.access;
import static snacks.lang.ast.AstFactory.apply;
import static snacks.lang.ast.AstFactory.assign;
import static snacks.lang.ast.AstFactory.begin;
import static snacks.lang.ast.AstFactory.closure;
import static snacks.lang.ast.AstFactory.constant;
import static snacks.lang.ast.AstFactory.constantDef;
import static snacks.lang.ast.AstFactory.declaration;
import static snacks.lang.ast.AstFactory.embrace;
import static snacks.lang.ast.AstFactory.exceptional;
import static snacks.lang.ast.AstFactory.expression;
import static snacks.lang.ast.AstFactory.func;
import static snacks.lang.ast.AstFactory.guard;
import static snacks.lang.ast.AstFactory.guards;
import static snacks.lang.ast.AstFactory.hurl;
import static snacks.lang.ast.AstFactory.initializer;
import static snacks.lang.ast.AstFactory.invokable;
import static snacks.lang.ast.AstFactory.loop;
import static snacks.lang.ast.AstFactory.matchConstant;
import static snacks.lang.ast.AstFactory.matchConstructor;
import static snacks.lang.ast.AstFactory.nop;
import static snacks.lang.ast.AstFactory.prop;
import static snacks.lang.ast.AstFactory.propDef;
import static snacks.lang.ast.AstFactory.record;
import static snacks.lang.ast.AstFactory.result;
import static snacks.lang.ast.AstFactory.sequence;
import static snacks.lang.ast.AstFactory.symbol;
import static snacks.lang.ast.AstFactory.tuple;
import static snacks.lang.ast.AstFactory.unit;
import static snacks.lang.parser.syntax.SyntaxFactory.id;
import static snacks.lang.parser.syntax.SyntaxFactory.importId;
import static snacks.lang.parser.syntax.SyntaxFactory.qid;
import static snacks.lang.parser.syntax.SyntaxFactory.type;

public class Translator implements SyntaxVisitor {

    private static final Pattern OPERATOR_PATTERN = Pattern.compile("^\\W+$");

    private final String module;
    private final Deque<SymbolEnvironment> environments;
    private final List<NamedNode> declarations;
    private final Map<String, String> aliases;
    private final Map<String, Type> parameters;
    private final List<String> wildcardImports;
    private final List<String> typeErrors;
    private final Map<Symbol, Locator> names;
    private final PatternCollection patterns;
    private AstNode result;
    private Type type;
    private int functionLevel;
    private NameSequence currentName;

    public Translator(SymbolEnvironment environment, String module) {
        this.module = module;
        this.environments = new ArrayDeque<>(asList(environment));
        this.declarations = new ArrayList<>();
        this.aliases = new HashMap<>();
        this.parameters = new LinkedHashMap<>();
        this.wildcardImports = new ArrayList<>(asList("snacks.lang", module));
        this.typeErrors = new ArrayList<>();
        this.names = new IdentityHashMap<>();
        this.patterns = new PatternCollection();
    }

    public void enterConstructor(Type type) {
        patterns.enterConstructor(type);
    }

    public void leaveConstructor() {
        patterns.leaveConstructor();
    }

    public List<NamedNode> translateModule(Symbol node) {
        translate(node);
        declarations.addAll(patterns.render());
        return new ArrayList<>(declarations);
    }

    @Override
    public void visitAccessExpression(AccessExpression node) {
        AstNode expression = translate(node.getExpression());
        String property = node.getProperty();
        for (Type type : expression.getType().decompose()) {
            if (type instanceof RecordType) {
                for (Property propertyType : ((RecordType) type).getProperties()) {
                    if (propertyType.getName().getValue().equals(property)) {
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
        if (!isBoolean(left)) {
            throw new TypeException("Left-hand side of LOGICAL AND is not a boolean: got " + left.getType());
        } else if (!isBoolean(right)) {
            throw new TypeException("Right-hand side of LOGICAL AND is not a boolean: got " + right.getType());
        } else {
            result = new LogicalAnd(left, right);
        }
    }

    @Override
    public void visitAnyMatcher(AnyMatcher node) {
        result = nop();
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
    public void visitCaptureMatcher(CaptureMatcher node) {
        AstNode value = currentArgument();
        define(new VariableLocator(node.getVariable()), value.getType());
        result = AstFactory.var(node.getVariable(), value);
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
    public void visitConstantDeclaration(ConstantDeclaration node) {
        result = constantDef(qualify(node.getName()));
        alias(node.getName(), qualify(node.getName()));
        define(new DeclarationLocator(qualify(node.getName()), EXPRESSION), result.getType());
        define(new DeclarationLocator(qualify(node.getName()), TYPE), result.getType());
    }

    @Override
    public void visitConstantMatcher(ConstantMatcher node) {
        Type type = translateType(node.getConstant());
        result = matchConstant(currentArgument(type), translate(node.getConstant()));
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
    public void visitConstructorMatcher(ConstructorMatcher node) {
        Type type = translateType(node.getConstructor());
        List<AstNode> argumentMatchers = new ArrayList<>();
        enterConstructor(type);
        for (int i = 0; i < node.getArgumentMatchers().size(); i++) {
            setCurrentProperty("_" + i);
            argumentMatchers.add(translate(node.getArgumentMatchers().get(i)));
        }
        result = matchConstructor(currentArgument(type), argumentMatchers);
        leaveConstructor();
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
        define(new VariableLocator(node.getArgument()), Types.var("E"));
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
        alias(node.getAlias(), join(segments, '.'));
    }

    @Override
    public void visitInitializerExpression(InitializerExpression node) {
        AstNode constructor = translate(node.getConstructor());
        Type type = constructor.getType();
        if (type instanceof RecordType) {
            throw new UnsupportedOperationException("Record copy syntax not yet implemented");
        } else if (type instanceof FunctionType) {
            while (type instanceof FunctionType) {
                type = resultOf(type);
            }
            if (type instanceof RecordType) {
                RecordType recordType = (RecordType) type;
                PropertyInitializers properties = translateProperties(node, recordType);
                properties.requireAll();
                SnacksList<Property> propertyTypes = recordType.getProperties();
                Iterator<Property> propertyIterator = propertyTypes.iterator();
                for (int i = 0; i < propertyTypes.size(); i++) {
                    Type resultType = null;
                    if (i == propertyTypes.size() - 1) {
                        resultType = type.expose();
                    }
                    constructor = apply(
                        constructor,
                        properties.getValue(propertyIterator.next().getName().getValue()),
                        resultType
                    );
                }
                result = constructor;
                return;
            }
        }
        throw new ParseException("Type mismatch: cannot initialize against non-record type");
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
            throw new TypeException("Unable to determine type of invokable:\n\u2022 " + join(typeErrors, "\n\u2022 "));
        }
        Type functionType = func(voidType(), body.getType());
        leaveScope();
        leaveFunction();
        if (functionLevel > 1) {
            Locator locator = names.get(node);
            if (locator == null) {
                String name = generateName();
                Collection<String> environment = getVariables();
                register(name, functionType);
                declarations.add(declaration(qualify(name), closure(environment, body)));
                locator = new ClosureLocator(qualify(name), environment);
                names.put(node, locator);
            }
            register(locator.getName().substring(locator.getName().lastIndexOf('.') + 1), functionType);
            result = new Reference(locator, functionType.expose());
        } else {
            result = invokable(body);
        }
    }

    @Override
    public void visitIteratorLoop(IteratorLoop node) {
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
    public void visitNamedPattern(NamedPattern node) {
        Locator locator = new DeclarationLocator(qualify(node.getName()), EXPRESSION);
        if (hasSignature(locator)) {
            patterns.beginPattern(node.getName(), locator, environment());
        } else {
            throw new ParseException("No signature defined for pattern: " + locator.getName());
        }
        patterns.acceptPattern((PatternCase) translate(node.getPattern()));
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
        if (!isBoolean(left)) {
            throw new TypeException("Left-hand side of LOGICAL OR is not a boolean: got " + left.getType());
        } else if (!isBoolean(right)) {
            throw new TypeException("Right-hand side of LOGICAL OR is not a boolean: got " + right.getType());
        } else {
            result = new LogicalOr(left, right);
        }
    }

    @Override
    public void visitPatternMatcher(PatternMatcher node) {
        enterPattern();
        List<AstNode> arguments = new ArrayList<>();
        int environments = 0;
        for (Symbol argument : node.getMatchers()) {
            enterScope();
            arguments.add(translate(argument));
            nextArgument();
            environments++;
        }
        AstNode body = translate(node.getBody());
        for (int i = 0; i < environments; i++) {
            leaveScope();
        }
        result = new PatternCase(arguments, body);
        leavePattern();
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
    public void visitPropertyMatcher(PropertyMatcher node) {
        setCurrentProperty(node.getName());
        result = translate(node.getMatcher());
    }

    @Override
    public void visitProtocolDeclaration(ProtocolDeclaration node) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public void visitProtocolImplementation(ProtocolImplementation node) {
        throw new UnsupportedOperationException(); // TODO
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
    public void visitQuotedOperator(QuotedOperator node) {
        result = reference(node.getName(), EXPRESSION);
    }

    @Override
    public void visitRecordDeclaration(RecordDeclaration node) {
        List<Property> properties = new ArrayList<>();
        for (Symbol property : node.getProperties()) {
            DeclaredProperty p = (DeclaredProperty) translate(property);
            properties.add(property(p.getName(), p.getType()));
        }
        result = record(qualify(node.getName()), parameters.values(), properties);
    }

    @Override
    public void visitRecordMatcher(RecordMatcher node) {
        Type type = translateType(node.getConstructor());
        List<AstNode> argumentMatchers = new ArrayList<>();
        enterConstructor(type);
        for (int i = 0; i < node.getPropertyMatchers().size(); i++) {
            argumentMatchers.add(translate(node.getPropertyMatchers().get(i)));
        }
        result = matchConstructor(currentArgument(type), argumentMatchers);
        leaveConstructor();
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
        Reference reference = new Reference(
            new DeclarationLocator(qualify(node.getIdentifier())),
            translateType(node.getType()).expose()
        );
        alias(node.getIdentifier(), reference.getLocator().getName());
        signature(reference);
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
        this.parameters.clear();
        List<Type> parameters = new ArrayList<>();
        for (String argumentName : node.getParameters()) {
            this.parameters.put(argumentName, createVariable(qualify(node.getName()) + '#' + argumentName));
            parameters.add(this.parameters.get(argumentName));
        }
        Locator locator = new DeclarationLocator(qualify(node.getName()), TYPE);
        alias(node.getName(), qualify(node.getName()));
        signature(new Reference(locator, recur(qualify(node.getName()))));
        List<NamedNode> variants = new ArrayList<>();
        if (isSingularType(node)) {
            NamedNode variant = (NamedNode) translate(node.getVariants().get(0));
            DeclaredType declaration = new DeclaredType(qualify(node.getName()), asList(variant));
            define(locator, variant.getType());
            defineConstructor(variant.getQualifiedName(), parameters, ((RecordType) variant.getType()).getProperties());
            declarations.add(declaration);
        } else {
            for (Symbol variant : node.getVariants()) {
                variants.add((NamedNode) translate(variant));
            }
            List<Type> types = new ArrayList<>();
            for (NamedNode variant : variants) {
                types.add(variant.getType());
            }
            Type parentType = new AlgebraicType(qualify(node.getName()), parameters, types);
            for (int i = 0; i < types.size(); i++) {
                NamedNode variant = variants.get(i);
                alias(variant.getSimpleName(), variant.getQualifiedName());
                if (types.get(i) instanceof RecordType) {
                    RecordType type = (RecordType) new TypeUnroller(types.get(i), parentType).unroll();
                    define(new DeclarationLocator(variant.getQualifiedName(), TYPE),
                        new RecordType(variant.getQualifiedName(), parentType.getArguments(), type.getProperties()));
                    defineConstructor(variant.getQualifiedName(), parentType.getArguments(), type.getProperties());
                    variants.set(i, new DeclaredRecord(variant.getQualifiedName(), parameters, fromList(type.getProperties())));
                } else {
                    define(new DeclarationLocator(variant.getQualifiedName(), TYPE), variant.getType());
                    defineConstant(variant.getQualifiedName(), variant.getType());
                }
            }
            DeclaredType declaration = new DeclaredType(qualify(node.getName()), variants);
            define(locator, parentType);
            declarations.add(declaration);
        }
    }

    @Override
    public void visitTypeReference(TypeReference node) {
        List<Type> typeArguments = new ArrayList<>();
        for (Symbol argument : node.getArguments()) {
            typeArguments.add(translateType(argument));
        }
        type = new ArgumentBinder(typeArguments).bind(translateType(node.getType()));
    }

    @Override
    public void visitTypeSpec(TypeSpec node) {
        result = referenceQualifiedIdentifier(node.getName(), TYPE);
    }

    @Override
    public void visitTypeVariable(TypeVariable node) {
        if (parameters.containsKey(node.getName())) {
            type = parameters.get(node.getName());
        } else {
            type = var(node.getName());
        }
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
        define(new VariableLocator(node.getName()), varType);
        specialize(varType);
        environment().unify(varType, value.getType());
        generify(varType);
        result = AstFactory.var(node.getName(), value);
    }

    @Override
    public void visitVarDeclaration(VarDeclaration node) {
        Type varType = createVariable();
        define(new VariableLocator(node.getName()), varType);
        specialize(varType);
        result = new VariableDeclaration(node.getName());
    }

    @Override
    public void visitWildcardImport(WildcardImport node) {
        addWildcardImport(join(((QualifiedIdentifier) node.getModule()).getSegments(), '.'));
    }

    private void addWildcardImport(String wildcardImport) {
        wildcardImports.add(wildcardImport);
    }

    private void alias(String alias, String qualifiedName) {
        aliases.put(alias, qualifiedName);
    }

    private void beginFunction() {
        functionLevel++;
    }

    private Type createVariable() {
        return environment().createVariable();
    }

    private Type createVariable(String name) {
        return new VariableType(name);
    }

    private AstNode currentArgument() {
        return patterns.currentArgument();
    }

    private Reference currentArgument(Type type) {
        return patterns.currentArgument(type);
    }

    private void define(Locator locator, Type type) {
        environment().define(new Reference(locator, type.expose()));
    }

    private void defineConstant(String name, Type type) {
        define(new DeclarationLocator(name, EXPRESSION), type);
        Symbol constructor = new ConstructorExpression(type(name.split("\\.")), new ArrayList<Symbol>());
        declarations.add(new DeclaredConstructor(name, namedDeclaration(name + "Constructor", constructor).getBody()));
    }

    private void defineConstructor(String name, Iterable<Type> typeArguments, SnacksList<Property> properties) {
        Type constructorType = new RecordType(name, typeArguments, properties);
        for (Property property : properties.reverse()) {
            constructorType = func(property.getType(), constructorType);
        }
        define(new DeclarationLocator(name, EXPRESSION), constructorType);
        Symbol constructor = type(name.split("\\."));
        List<Symbol> arguments = new ArrayList<>();
        for (Property property : properties) {
            arguments.add(id(property.getName().getValue()));
        }
        constructor = new ConstructorExpression(constructor, arguments);
        for (Property property : properties.reverse()) {
            constructor = new FunctionLiteral(
                new Argument(property.getName().getValue(), typeToSymbol(property.getType())),
                constructor,
                null
            );
        }
        declarations.add(new DeclaredConstructor(name, namedDeclaration(name + "Constructor", constructor).getBody()));
    }

    private void enterPattern() {
        enterScope();
        patterns.enterScope(environment());
    }

    private void enterScope() {
        environments.push(environment().extend());
    }

    private SymbolEnvironment environment() {
        return environments.peek();
    }

    private Locator findWildcard(String value, SnackKind kind) {
        for (String module : wildcardImports) {
            Locator locator = new DeclarationLocator(module + '.' + value, kind);
            if (isDefined(locator)) {
                return locator;
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
        if (node instanceof Identifier) {
            return getOperator(((Identifier) node).getName());
        } else if (node instanceof QuotedOperator) {
            return new Operator(LEFT, 10, 2, ((QuotedOperator) node).getName());
        } else {
            throw new IllegalArgumentException("Cannot get operator for node " + node.getClass());
        }
    }

    private Operator getOperator(String name) {
        return environment().getOperator(name);
    }

    private Type getSignature(Locator locator) {
        return environment().getSignature(locator);
    }

    private Collection<String> getVariables() {
        return environment().getVariables();
    }

    private boolean hasSignature(Locator locator) {
        return environment().hasSignature(locator);
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
            if (environment().unify(pair.functionType, func(pair.argumentType, resultType))) {
                resultTypes.add(resultType.expose());
                argumentTypes.add(pair.argumentType.expose());
            }
        }
        if (resultTypes.isEmpty()) {
            typeErrors.add("Could not apply function " + functionType + " to argument " + argumentType);
        }
        argumentType.bind(union(argumentTypes));
        return union(resultTypes);
    }

    private boolean isBoolean(AstNode expression) {
        return environment().unify(booleanType(), expression.getType());
    }

    private boolean isDefined(Locator locator) {
        return environment().isDefined(locator);
    }

    private boolean isIdentifierOperator(String name) {
        reference(name, EXPRESSION);
        if (!isOperator(name)) {
            if (OPERATOR_PATTERN.matcher(name).find()) {
                environment().registerInfix(10, LEFT, name);
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean isOperator(Symbol node) {
        return node instanceof QuotedOperator
            || node instanceof Identifier && isIdentifierOperator(((Identifier) node).getName());
    }

    private boolean isOperator(String name) {
        return environment().isOperator(name);
    }

    private boolean isPrefix(Symbol symbol) {
        return getOperator(symbol).isPrefix();
    }

    private boolean isSingularType(TypeDeclaration node) {
        List<Symbol> variants = node.getVariants();
        return variants.size() == 1
            && variants.get(0) instanceof RecordDeclaration
            && ((RecordDeclaration) variants.get(0)).getName().equals(node.getName());
    }

    private String javaClass(Symbol symbol) {
        TypeSpec type = (TypeSpec) symbol;
        QualifiedIdentifier id = type.getName();
        return join(id.getSegments(), ".");
    }

    private void leaveFunction() {
        functionLevel--;
    }

    private void leavePattern() {
        patterns.leaveScope();
        leaveScope();
    }

    private void leaveScope() {
        environments.pop();
    }

    private void matchTypes(AstNode left, AstNode right) {
        if (!environment().unify(left.getType(), right.getType())) {
            throw new TypeException("Type mismatch: " + left.getType() + " != " + right.getType());
        }
    }

    private DeclaredExpression namedDeclaration(String name, Symbol expression) {
        functionLevel = 0;
        if (expression instanceof InvokableLiteral) {
            functionLevel++; // TODO hack
        }
        typeErrors.clear();
        reserveName(name.substring(name.lastIndexOf('.') + 1));
        AstNode body = translate(expression);
        if (!body.isInvokable()) {
            body = expression(body);
        }
        Locator locator = new DeclarationLocator(qualify(name));
        DeclaredExpression declaration = declaration(qualify(name), body);
        if (declaration.getType().decompose().isEmpty()) {
            throw new TypeException(join(typeErrors, ";\n"));
        }
        if (hasSignature(locator)) {
            Type type = getSignature(locator);
            if (environment().unify(declaration.getType(), type)) {
                declaration.setType(type);
            } else {
                throw new TypeException("Type mismatch: " + declaration.getType() + " != " + type);
            }
        }
        if (declarations.contains(declaration)) {
            throw new UndefinedSymbolException("Cannot redefine " + declaration.getQualifiedName());
        }
        if (isOperator(declaration.getSimpleName())) {
            declaration.setOperator(getOperator(declaration.getSimpleName()));
        }
        alias(declaration.getSimpleName(), declaration.getQualifiedName());
        define(locator, declaration.getType());
        return declaration;
    }

    private void nextArgument() {
        patterns.nextArgument();
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

    private String qualify(String name) {
        return module + '.' + name;
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
        if (stack.size() > 1) {
            List<String> names = new ArrayList<>();
            for (Symbol symbol : stack) {
                names.add(symbol.getClass().getSimpleName());
            }
            throw new ParseException("Failed to shuffle message: got " + join(names, ", "));
        }
        return stack.pop();
    }

    private Reference reference(String value, SnackKind kind) {
        if (kind == TYPE && parameters.containsKey(value)) {
            return new Reference(new DeclarationLocator(value, TYPE), parameters.get(value));
        } else {
            Locator locator = new VariableLocator(value);
            if (!isDefined(locator)) {
                if (aliases.containsKey(value)) {
                    locator = new DeclarationLocator(aliases.get(value), kind);
                } else {
                    locator = findWildcard(value, kind);
                }
            }
            return new Reference(locator, typeOf(locator));
        }
    }

    private AstNode referenceQualifiedIdentifier(QualifiedIdentifier node, SnackKind kind) {
        List<String> segments = node.getSegments();
        if (segments.size() > 1) {
            Locator locator = new DeclarationLocator(join(segments, '.'), kind);
            if (isDefined(locator)) {
                return new Reference(locator, typeOf(locator));
            } else {
                throw new UndefinedSymbolException("Symbol " + locator.getName() + " is not defined");
            }
        } else {
            return reference(segments.get(0), kind);
        }
    }

    private void register(String name, Type type) {
        Locator locator = new DeclarationLocator(qualify(name));
        alias(name, qualify(name));
        define(locator, type);
    }

    private void reserveName(String name) {
        currentName = new NameSequence(name);
    }

    private void setCurrentProperty(String property) {
        patterns.setProperty(property);
    }

    private Symbol shuffleElements(List<Symbol> elements) {
        Deque<Symbol> input = new ArrayDeque<>(elements);
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
                        output.push(toSymbol(operators.pop()));
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
            output.push(toSymbol(operators.pop()));
        }
        return reduceOperations(output);
    }

    private Symbol shuffleMessage(Message message) {
        List<Symbol> elements = message.getElements();
        if (elements.size() == 1) {
            return elements.get(0);
        } else {
            return shuffleElements(elements);
        }
    }

    private void signature(Reference reference) {
        environment().signature(reference);
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

    private Symbol toSymbol(Operator operator) {
        if (operator.getFixity() == LEFT && operator.getPrecedence() == 10) {
            return new QuotedOperator(operator.getName());
        } else {
            return new Identifier(operator.getName());
        }
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
        if (isInsideClosure()) {
            leaveFunction();
            Locator locator = names.get(node);
            if (locator == null) {
                String name = generateName();
                Collection<String> environment = getVariables();
                register(name, functionType);
                declarations.add(declaration(qualify(name), closure(functionType, argument.getName(), body, environment)));
                locator = new ClosureLocator(qualify(name), environment);
                names.put(node, locator);
            }
            register(locator.getName().substring(locator.getName().lastIndexOf('.') + 1), functionType);
            return new Reference(locator, functionType);
        } else {
            return func(functionType, argument.getName(), body);
        }
    }

    private boolean isInsideClosure() {
        return functionLevel > 1;
    }

    private PropertyInitializers translateProperties(InitializerExpression node, RecordType recordType) {
        List<Property> matchedProperties = new ArrayList<>();
        Map<String, PropertyInitializer> initializers = new HashMap<>();
        for (Symbol n : node.getProperties()) {
            PropertyInitializer property = (PropertyInitializer) translate(n);
            if (initializers.containsKey(property.getName())) {
                throw new DuplicatePropertyException("Duplicate property initializer: " + property.getName());
            }
            boolean found = false;
            for (Property propertyType : recordType.getProperties()) {
                if (propertyType.getName().getValue().equals(property.getName())) {
                    verifyAssignmentType(propertyType.getType(), property.getType());
                    matchedProperties.add(propertyType);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new TypeException("Property " + recordType.getName() + "#" + property.getName() + " does not exist");
            }
            initializers.put(property.getName(), property);
        }
        return new PropertyInitializers(recordType, initializers, matchedProperties);
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

    private Type typeOf(Locator locator) {
        return environment().typeOf(locator);
    }

    private Symbol typeToSymbol(Type type) {
        String name = type.getName().getValue();
        if (type.expose() instanceof VariableType) {
            if (name.contains("#")) {
                return type(name.substring(name.lastIndexOf('#') + 1));
            } else {
                return type(name);
            }
        } else {
            return type(name.split("\\."));
        }
    }

    private boolean unifyFunctionResult(Type functionType, Type declaredResultType) {
        if (functionType.decompose().size() == 1) {
            Type actualResultType = resultOf(functionType);
            return environment().unify(declaredResultType, actualResultType);
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
        if (!environment().unify(targetType, valueType)) {
            throw new TypeException("Type mismatch: " + targetType + " != " + valueType);
        }
        generify(targetType);
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

    private class PropertyInitializers {

        private final RecordType record;
        private final Map<String, PropertyInitializer> initializers;
        private final List<Property> matchedProperties;

        private PropertyInitializers(
            RecordType recordType,
            Map<String, PropertyInitializer> initializers,
            List<Property> matchedProperties
        ) {
            this.record = recordType;
            this.initializers = initializers;
            this.matchedProperties = matchedProperties;
        }

        private AstNode getValue(String name) {
            return initializers.get(name).getValue();
        }

        private void requireAll() {
            List<Property> missingProperties = fromList(record.getProperties());
            missingProperties.removeAll(matchedProperties);
            if (matchedProperties.size() != record.getProperties().size()) {
                List<String> propertyNames = new ArrayList<>();
                for (Property property : missingProperties) {
                    propertyNames.add(property.getName().getValue());
                }
                throw new MissingPropertyException("Missing properties: " + join(propertyNames, ", "));
            }
        }
    }
}
