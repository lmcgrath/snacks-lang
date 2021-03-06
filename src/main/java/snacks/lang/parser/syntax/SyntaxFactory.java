package snacks.lang.parser.syntax;

import static java.lang.System.arraycopy;
import static java.util.Arrays.asList;
import static java.util.Collections.addAll;
import static snacks.lang.Fixity.LEFT;
import static snacks.lang.Fixity.NONE;
import static snacks.lang.Fixity.RIGHT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import beaver.Symbol;
import snacks.lang.Operator;

public final class SyntaxFactory {

    public static Symbol access(Symbol expression, String property) {
        return new AccessExpression(expression, property);
    }

    public static Symbol apply(Symbol function, Symbol argument) {
        return new ApplyExpression(function, argument);
    }

    public static Symbol arg(String name) {
        return new Argument(name, null);
    }

    public static Symbol arg(String name, Symbol type) {
        return new Argument(name, type);
    }

    @SafeVarargs
    public static <T> T[] array(T... elements) {
        return elements;
    }

    public static Symbol assign(Symbol target, Symbol value) {
        return new AssignmentExpression(target, value);
    }

    public static Symbol begin(Symbol expression, Symbol[] embraceCases) {
        return new ExceptionalExpression(null, expression, embraceCases, null);
    }

    public static Symbol begin(Symbol expression, Symbol ensureCase) {
        return new ExceptionalExpression(null, expression, null, ensureCase);
    }

    public static Symbol begin(Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        return new ExceptionalExpression(null, expression, embraceCases, ensureCase);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression) {
        return new ExceptionalExpression(usings, expression, null, null);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression, Symbol[] embraceCases) {
        return new ExceptionalExpression(usings, expression, embraceCases, null);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression, Symbol ensureCase) {
        return new ExceptionalExpression(usings, expression, null, ensureCase);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        return new ExceptionalExpression(usings, expression, embraceCases, ensureCase);
    }

    public static Symbol block(Symbol... elements) {
        return new Block(elements);
    }

    public static Symbol condition(Symbol condition, Symbol expression) {
        return new ConditionCase(condition, expression);
    }

    public static Symbol conditional(Symbol... cases) {
        return new Conditional(asList(cases));
    }

    public static Symbol conditional(Symbol head, Symbol[] tail, Symbol defaultCase) {
        List<Symbol> elements = new ArrayList<>();
        elements.add(head);
        if (tail != null) {
            addAll(elements, tail);
        }
        if (defaultCase instanceof VisitableSymbol) {
            elements.add(defaultCase);
        }
        return new Conditional(elements);
    }

    public static Symbol constDef(String name) {
        return new ConstantDeclaration(name);
    }

    public static Symbol def(String name, Symbol expression) {
        return new ExpressionDeclaration(name, expression);
    }

    public static Symbol embrace(String var, Symbol expression) {
        return new EmbraceCase(var, null, expression);
    }

    public static Symbol embrace(String var, Symbol type, Symbol expression) {
        return new EmbraceCase(var, type, expression);
    }

    public static MapEntry entry(Symbol key, Symbol value) {
        return new MapEntry(key, value);
    }

    public static Symbol from(Symbol module, Symbol... subImports) {
        return new FromImport(module, subImports);
    }

    public static Symbol fsig(Symbol argument, Symbol result) {
        return new FunctionSignature(argument, result);
    }

    public static Symbol ftype(QualifiedIdentifier id) {
        List<String> segments = id.getSegments();
        if (segments.size() == 1 && Character.isLowerCase(segments.get(0).charAt(0))) {
            return typeVar(segments.get(0));
        } else {
            return type(id);
        }
    }

    public static Symbol func(Symbol arg, Symbol body) {
        return new FunctionLiteral(arg, body, null);
    }

    public static Symbol func(Symbol arg, Symbol body, Symbol type) {
        return new FunctionLiteral(arg, body, type);
    }

    public static Symbol hurl(Symbol expression) {
        return new HurlExpression(expression);
    }

    public static Symbol id(String name) {
        return new Identifier(name);
    }

    public static Symbol implement(String name, Collection<Symbol> arguments, Collection<Symbol> members) {
        return new ProtocolImplementation(name, arguments, members);
    }

    public static Symbol derives(Symbol... protocols) {
        return new DerivesProtocols(asList(protocols));
    }

    public static Symbol importId(Symbol id) {
        return new Import(id, ((QualifiedIdentifier) id).getLastSegment());
    }

    public static Symbol importId(Symbol id, String alias) {
        return new Import(id, alias);
    }

    public static Symbol importWildcard(Symbol id) {
        return new WildcardImport(id);
    }

    public static Symbol initializer(Symbol constructor, Symbol... properties) {
        return new InitializerExpression(constructor, properties);
    }

    public static Symbol initializer(Symbol constructor, Symbol property, Symbol[] properties) {
        Symbol[] properties2 = new Symbol[properties.length + 1];
        properties2[0] = property;
        arraycopy(properties, 0, properties2, 1, properties.length);
        return new InitializerExpression(constructor, properties2);
    }

    public static Symbol invokable(Symbol body) {
        return new InvokableLiteral(body);
    }

    public static Symbol leftOp(String id, int precedence) {
        return new OperatorDeclaration(new Operator(LEFT, precedence, 2, id));
    }

    public static Symbol list(Symbol... entries) {
        Symbol tail = qid("snacks", "lang", "EmptyList");
        for (int i = entries.length - 1; i >= 0; i--) {
            tail = apply(apply(qid("snacks", "lang", "ListEntry"), entries[i]), tail);
        }
        return tail;
    }

    public static Symbol literal(String value) {
        return new StringLiteral(value);
    }

    public static Symbol literal(char value) {
        return new CharacterLiteral(value);
    }

    public static Symbol literal(int value) {
        return new IntegerLiteral(value);
    }

    public static Symbol literal(double value) {
        return new DoubleLiteral(value);
    }

    public static Symbol literal(boolean value) {
        return new BooleanLiteral(value);
    }

    public static Symbol loop(Symbol condition, Symbol body) {
        return new LoopExpression(condition, body);
    }

    public static Symbol loop(String var, Symbol elements, Symbol body) {
        return new IteratorLoop(var, elements, body);
    }

    public static Symbol map(MapEntry... entries) {
        Symbol empty = qid("snacks", "lang", "EmptyMap");
        Symbol entry = qid("snacks", "lang", "MapEntry");
        Symbol tail = empty;
        for (int i = entries.length - 1; i >= 0; i--) {
            tail = apply(apply(apply(apply(entry, entries[i].getKey()), entries[i].getValue()), tail), empty);
        }
        return tail;
    }

    public static Symbol matchAny() {
        return new AnyMatcher();
    }

    public static Symbol matchCapture(String argument) {
        return new CaptureMatcher(argument);
    }

    public static Symbol matchConstant(Symbol constant) {
        return new ConstantMatcher(constant);
    }

    public static Symbol matchConstructor(Symbol constructor, Collection<Symbol> argumentMatchers) {
        return new ConstructorMatcher(constructor, argumentMatchers);
    }

    public static Symbol matchName(QualifiedIdentifier id) {
        List<String> segments = id.getSegments();
        if (segments.size() == 1 && Character.isLowerCase(segments.get(0).charAt(0))) {
            return matchCapture(segments.get(0));
        } else {
            return matchConstant(id);
        }
    }

    public static Symbol matchProperty(String name, Symbol matcher) {
        return new PropertyMatcher(name, matcher);
    }

    public static Symbol matchRecord(Symbol constructor, Collection<Symbol> propertyMatchers) {
        return new RecordMatcher(constructor, propertyMatchers);
    }

    public static Symbol member(String name, Symbol body) {
        return new ProtocolMember(name, body);
    }

    public static Symbol module(Symbol... elements) {
        return new Module(elements);
    }

    public static Symbol msg(Symbol... elements) {
        if (elements.length == 1) {
            return elements[0];
        } else {
            return new Message(elements);
        }
    }

    public static Symbol namedPattern(String name, Symbol pattern) {
        return new NamedPattern(name, pattern);
    }

    public static Symbol nop() {
        return new NopExpression();
    }

    public static Symbol op(String name, int precedence) {
        return new OperatorDeclaration(new Operator(NONE, precedence, 2, name));
    }

    public static Symbol pattern(Collection<Symbol> arguments, Symbol body) {
        return new PatternMatcher(arguments, body);
    }

    public static Symbol prefix(String name, int precedence) {
        return new OperatorDeclaration(new Operator(RIGHT, precedence, 1, name));
    }

    public static Symbol propDef(String name, Symbol type) {
        return new PropertyDeclaration(name, type);
    }

    public static Symbol property(String name, Symbol value) {
        return new PropertyExpression(name, value);
    }

    public static Symbol protocol(String name, Collection<String> arguments, Collection<Symbol> members) {
        return new ProtocolDeclaration(name, arguments, members);
    }

    public static QualifiedIdentifier qid(QualifiedIdentifier id, String segment) {
        return new QualifiedIdentifier(id, segment);
    }

    public static QualifiedIdentifier qid(String... ids) {
        return new QualifiedIdentifier(ids);
    }

    public static Symbol qop(String id) {
        return new QuotedOperator(id);
    }

    public static Symbol quoted(String id) {
        return new QuotedIdentifier(id);
    }

    public static Symbol recordDef(String name, Symbol... properties) {
        return recordDef(name, asList(properties));
    }

    public static Symbol recordDef(String name, Collection<Symbol> properties) {
        return new RecordDeclaration(name, properties);
    }

    public static Symbol regex(List<Symbol> elements, Set<Character> options) {
        return new RegexLiteral(elements, options);
    }

    public static Symbol result(Symbol expression) {
        return new Result(expression);
    }

    public static Symbol rightOp(String id, int precedence) {
        return new OperatorDeclaration(new Operator(RIGHT, precedence, 2, id));
    }

    public static Symbol set(Symbol element, Symbol[] elements) {
        Symbol[] newElements = new Symbol[elements.length + 1];
        newElements[0] = element;
        arraycopy(elements, 0, newElements, 1, elements.length);
        return set(newElements);
    }

    public static Symbol set(Symbol... entries) {
        Symbol empty = qid("snacks", "lang", "EmptySet");
        Symbol entry = qid("snacks", "lang", "SetEntry");
        Symbol list = qid("snacks", "lang", "ListEntry");
        Symbol emptyList = qid("snacks", "lang", "EmptyList");
        Symbol hashOf = qid("snacks", "lang", "hashOf");
        Symbol tail = empty;
        for (int i = entries.length - 1; i >= 0; i--) {
            Symbol elements = apply(apply(list, entries[i]), emptyList);
            Symbol hash = apply(hashOf, entries[i]);
            tail = apply(apply(apply(apply(entry, hash), elements), tail), empty);
        }
        return tail;
    }

    public static Symbol sig(String identifier, Symbol type) {
        return new Signature(identifier, type);
    }

    public static Symbol sub(String id) {
        return new SubImport(id, id);
    }

    public static Symbol sub(String id, String alias) {
        return new SubImport(id, alias);
    }

    public static Symbol suffix(Symbol target, Symbol condition) {
        return conditional(condition(condition, target), null, nop());
    }

    public static Symbol symbol(String name) {
        return new SymbolLiteral(name);
    }

    public static Symbol tsig(Symbol... ids) {
        return tsig(asList(ids));
    }

    public static Symbol tsig(Collection<Symbol> ids) {
        return new TupleSignature(ids);
    }

    public static Symbol tuple(Symbol... elements) {
        return new TupleLiteral(elements);
    }

    public static Symbol tuple(Symbol element, Symbol[] elements) {
        return new TupleLiteral(element, elements);
    }

    public static Symbol type(QualifiedIdentifier id) {
        return new TypeSpec(id);
    }

    public static Symbol type(String... segments) {
        return type(qid(segments));
    }

    public static Symbol typeDef(String name, Collection<Symbol> definition) {
        return typeDef(name, new ArrayList<String>(), definition);
    }

    public static Symbol typeDef(String name, Collection<String> parameters, Collection<Symbol> definition) {
        return new TypeDeclaration(name, parameters, definition);
    }

    public static Symbol typeRef(Symbol type, Collection<Symbol> parameters) {
        return new TypeReference(type, parameters);
    }

    public static Symbol typeVar(String name) {
        return new TypeVariable(name);
    }

    public static Symbol unary(String operator, Symbol operand) {
        return apply(id(operator), operand);
    }

    public static Symbol unit() {
        return UnitLiteral.INSTANCE;
    }

    public static Symbol use(String var, Symbol value) {
        return new Using(var, value);
    }

    public static Symbol using(Symbol value) {
        return new Using(null, value);
    }

    public static Symbol var(String name) {
        return new VarDeclaration(name);
    }

    public static Symbol var(String name, Symbol value) {
        return new Var(name, value);
    }

    private SyntaxFactory() {
        // intentionally empty
    }
}
