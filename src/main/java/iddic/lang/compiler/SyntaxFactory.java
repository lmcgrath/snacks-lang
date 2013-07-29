package iddic.lang.compiler;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import iddic.lang.compiler.syntax.*;

public class SyntaxFactory {

    public static Access access(String value) {
        return new Access(value);
    }

    public static IdentifierAlias alias(String alias) {
        return new IdentifierAlias(alias);
    }

    public static ArgumentsList args(SyntaxNode... arguments) {
        return new ArgumentsList(arguments);
    }

    public static ArgumentsList args(Collection<SyntaxNode> arguments) {
        return new ArgumentsList(arguments);
    }

    public static FunctionArguments args(String... arguments) {
        return new FunctionArguments(arguments);
    }

    public static BinaryExpression binary(String operator, SyntaxNode left, SyntaxNode right) {
        return new BinaryExpression(operator, left, right);
    }

    public static BlockExpression block(SyntaxNode... members) {
        return new BlockExpression(members);
    }

    public static BlockExpression block(Collection<SyntaxNode> members) {
        return new BlockExpression(members);
    }

    public static Conditional conditional(SyntaxNode head, SyntaxNode... tail) {
        return new Conditional(head, tail);
    }

    public static Conditional conditional(SyntaxNode head, List<SyntaxNode> tail) {
        return new Conditional(head, tail);
    }

    public static Declaration declaration(String id, SyntaxNode expression) {
        return new Declaration(id, expression);
    }

    public static Declaration declaration(Declaration declaration, MetaAnnotation... meta) {
        return new Declaration(declaration, meta);
    }

    public static Declaration declaration(String id, SyntaxNode expression, MetaAnnotation... meta) {
        return new Declaration(declaration(id, expression), meta);
    }

    public static Declaration declaration(String id, SyntaxNode expression, Collection<MetaAnnotation> meta) {
        return new Declaration(declaration(id, expression), meta);
    }

    public static Embrace embrace(SyntaxNode begin, SyntaxNode embrace, SyntaxNode ensure) {
        return new Embrace(begin, asList(embrace), ensure);
    }

    public static Embrace embrace(SyntaxNode begin, SyntaxNode[] embrace, SyntaxNode ensure) {
        return new Embrace(begin, embrace, ensure);
    }

    public static Embrace embrace(SyntaxNode begin, Collection<SyntaxNode> embrace, SyntaxNode ensure) {
        return new Embrace(begin, embrace, ensure);
    }

    public static MapElement entry(SyntaxNode key, SyntaxNode value) {
        return new MapElement(key, value);
    }

    public static FalsyCondition falsy(SyntaxNode condition, SyntaxNode expression) {
        return new FalsyCondition(condition, expression);
    }

    public static CompoundImportExpression from(QualifiedIdentifier identifier, SubImport... subs) {
        return new CompoundImportExpression(identifier, subs);
    }

    public static FunctionLiteral func(SyntaxNode arguments, SyntaxNode expression) {
        return new FunctionLiteral(arguments, expression);
    }

    public static FunctionLiteral func(String argument, SyntaxNode expression) {
        return new FunctionLiteral(args(argument), expression);
    }

    public static HurlExpression hurl(SyntaxNode expression) {
        return new HurlExpression(expression);
    }

    public static Identifier id(String value) {
        return new Identifier(value);
    }

    public static ImportExpression imports(QualifiedIdentifier id, IdentifierAlias alias) {
        return new ImportExpression(id, alias);
    }

    public static IndexAccess index(SyntaxNode... arguments) {
        return new IndexAccess(args(arguments));
    }

    public static IndexAccess index(Collection<SyntaxNode> arguments) {
        return new IndexAccess(args(arguments));
    }

    public static Interpolation interpolation(SyntaxNode... elements) {
        return new Interpolation(elements);
    }

    public static Interpolation interpolation(Collection<SyntaxNode> elements) {
        return new Interpolation(elements);
    }

    public static ListLiteral list(SyntaxNode... elements) {
        return new ListLiteral(elements);
    }

    public static ListLiteral list(Collection<SyntaxNode> elements) {
        return new ListLiteral(elements);
    }

    public static StringLiteral literal(String value) {
        return new StringLiteral(value);
    }

    public static DoubleLiteral literal(double value) {
        return new DoubleLiteral(value);
    }

    public static CharacterLiteral literal(char value) {
        return new CharacterLiteral(value);
    }

    public static IntegerLiteral literal(int value) {
        return new IntegerLiteral(value);
    }

    public static BooleanLiteral literal(boolean value) {
        return new BooleanLiteral(value);
    }

    public static MapLiteral map() {
        return new MapLiteral();
    }

    public static MapLiteral map(MapElement... elements) {
        return new MapLiteral(elements);
    }

    public static MetaAnnotation meta(String name, SyntaxNode value) {
        return new MetaAnnotation(name, value);
    }

    public static ModuleDeclaration module(SyntaxNode... declarations) {
        return new ModuleDeclaration(declarations);
    }

    public static ModuleDeclaration module(Collection<SyntaxNode> declarations) {
        return new ModuleDeclaration(declarations);
    }

    public static NothingLiteral nothing() {
        return new NothingLiteral();
    }

    public static QualifiedIdentifier qualifiedId(String... segments) {
        return new QualifiedIdentifier(segments);
    }

    public static QualifiedIdentifier qualifiedId(Collection<String> segments) {
        return new QualifiedIdentifier(segments);
    }

    public static Regex regex(SyntaxNode expression, Set<String> options) {
        return new Regex(expression, options);
    }

    public static ReturnExpression result(SyntaxNode result) {
        return new ReturnExpression(result);
    }

    public static RootIdentifier root(String[] segments) {
        return new RootIdentifier(segments);
    }

    public static Selector select(SyntaxNode expression, SyntaxNode selector) {
        return new Selector(expression, selector);
    }

    public static SetLiteral set(SyntaxNode... elements) {
        return new SetLiteral(elements);
    }

    public static SetLiteral set(Collection<SyntaxNode> elements) {
        return new SetLiteral(elements);
    }

    public static SubImport sub(String identifier, IdentifierAlias alias) {
        return new SubImport(identifier, alias);
    }

    public static SymbolLiteral symbol(String value) {
        return new SymbolLiteral(value);
    }

    public static TruthyCondition truthy(SyntaxNode condition, SyntaxNode expression) {
        return new TruthyCondition(condition, expression);
    }

    public static TupleLiteral tuple(SyntaxNode... elements) {
        return new TupleLiteral(elements);
    }

    public static TupleLiteral tuple(Collection<SyntaxNode> elements) {
        return new TupleLiteral(elements);
    }

    public static UnaryExpression unary(String operator, SyntaxNode expression) {
        return new UnaryExpression(operator, expression);
    }

    public static VariableDeclaration var(String variable, SyntaxNode expression) {
        return new VariableDeclaration(variable, expression);
    }

    public static XRangeLiteral xrange(SyntaxNode lowerLimit, SyntaxNode upperLimit) {
        return new XRangeLiteral(lowerLimit, upperLimit);
    }
}
