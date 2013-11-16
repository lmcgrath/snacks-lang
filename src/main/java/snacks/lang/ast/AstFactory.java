package snacks.lang.ast;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import snacks.lang.Type;
import snacks.lang.Type.RecordType.Property;

public final class AstFactory {

    public static AstNode access(AstNode expression, String property, Type type) {
        return new Access(expression, property, type);
    }

    public static AstNode apply(AstNode function, AstNode argument, Type type) {
        return new Apply(function, argument, type);
    }

    public static AstNode assign(AstNode left, AstNode right) {
        return new Assign(left, right);
    }

    public static AstNode begin(AstNode body) {
        return new Begin(body);
    }

    public static AstNode closure(String variable, Collection<String> environment, AstNode expression, Type type) {
        return new FunctionClosure(variable, environment, expression, type);
    }

    public static AstNode closure(Collection<String> environment, AstNode expression) {
        return new Closure(environment, expression);
    }

    public static AstNode constant(boolean value) {
        return new BooleanConstant(value);
    }

    public static AstNode constant(char value) {
        return new CharacterConstant(value);
    }

    public static AstNode constant(double value) {
        return new DoubleConstant(value);
    }

    public static AstNode constant(int value) {
        return new IntegerConstant(value);
    }

    public static AstNode constant(String value) {
        return new StringConstant(value);
    }

    public static AstNode constantDef(String qualifiedName) {
        return new DeclaredConstant(qualifiedName);
    }

    public static DeclaredExpression declaration(String qualifiedName, AstNode body) {
        return new DeclaredExpression(qualifiedName, body);
    }

    public static AstNode embrace(String var, String javaClass, AstNode body) {
        return new Embrace(var, javaClass, body);
    }

    public static AstNode exceptional(AstNode begin, List<AstNode> embraces, AstNode ensure) {
        return new Exceptional(begin, embraces, ensure);
    }

    public static AstNode expression(AstNode value) {
        return new ExpressionConstant(value);
    }

    public static AstNode func(Type type, String variable, AstNode expression) {
        return new Function(variable, expression, type);
    }

    public static AstNode guard(AstNode condition, AstNode expression) {
        return new GuardCase(condition, expression);
    }

    public static AstNode guards(Collection<AstNode> cases) {
        return new GuardCases(cases);
    }

    public static AstNode hurl(AstNode body) {
        return new Hurl(body);
    }

    public static AstNode initializer(AstNode constructor, List<AstNode> arguments) {
        return new Initializer(constructor, arguments);
    }

    public static AstNode invokable(AstNode body) {
        return new VoidFunction(body);
    }

    public static AstNode is(AstNode left, AstNode right) {
        return new ReferencesEqual(left, right);
    }

    public static AstNode loop(AstNode condition, AstNode body) {
        return new Loop(condition, body);
    }

    public static AstNode matchConstant(Reference reference, AstNode constant) {
        return new MatchConstant(reference, constant);
    }

    public static AstNode matchConstructor(Reference reference, Collection<AstNode> parameters) {
        return new MatchConstructor(reference, parameters);
    }

    public static AstNode nop() {
        return Nop.INSTANCE;
    }

    public static PatternCase pattern(Collection<AstNode> matchCases, AstNode body) {
        return new PatternCase(matchCases, body);
    }

    public static AstNode patterns(Type type, Collection<PatternCase> patterns) {
        return new PatternCases(type, patterns);
    }

    public static AstNode prop(String name, AstNode value) {
        return new PropertyInitializer(name, value);
    }

    public static AstNode propDef(String name, Type type) {
        return new DeclaredProperty(name, type);
    }

    public static Reference reference(Locator locator, Type type) {
        return new Reference(locator, type);
    }

    public static NamedNode record(String qualifiedName, Collection<Type> parameters, Collection<Property> properties) {
        return new DeclaredRecord(qualifiedName, parameters, properties);
    }

    public static AstNode result(AstNode value) {
        return new Result(value);
    }

    public static AstNode sequence(AstNode... elements) {
        return sequence(asList(elements));
    }

    public static AstNode sequence(List<AstNode> elements) {
        return new Sequence(elements);
    }

    public static AstNode symbol(String name) {
        return new SymbolConstant(name);
    }

    public static AstNode tuple(List<AstNode> elements) {
        return new TupleInitializer(elements);
    }

    public static AstNode var(String name) {
        return new VariableDeclaration(name);
    }

    public static AstNode var(String name, AstNode value) {
        return new Assign(var(name), value);
    }

    public static AstNode var(String name, Type type) {
        return new DeclaredArgument(name, type);
    }

    public static AstNode unit() {
        return UnitConstant.INSTANCE;
    }

    private AstFactory() {
        // intentionally empty
    }
}
