package snacks.lang.compiler;

import java.util.List;
import java.util.Set;
import beaver.Symbol;
import snacks.lang.compiler.syntax.*;

public final class SyntaxFactory {

    public static Symbol access(Symbol expression, String property) {
        return new AccessExpression(expression, property);
    }

    public static Symbol annotation(String[] id, Symbol value) {
        return new Annotation(id, value);
    }

    public static Symbol apply(Symbol function, Symbol... arguments) {
        return new ArgumentsExpression(function, arguments);
    }

    public static Symbol arg(String name, Symbol type) {
        return new Argument(name, type);
    }

    public static Symbol begin(Symbol expression, Symbol[] embraceCases) {
        return new Exceptional(null, expression, embraceCases, null);
    }

    public static Symbol begin(Symbol expression, Symbol ensureCase) {
        return new Exceptional(null, expression, null, ensureCase);
    }

    public static Symbol begin(Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        return new Exceptional(null, expression, embraceCases, ensureCase);
    }

    public static Symbol binary(String operator, Symbol left, Symbol right) {
        return new BinaryExpression(operator, left, right);
    }

    public static Symbol block(Symbol... elements) {
        return new Block(elements);
    }

    public static Symbol dcase(Symbol expression) {
        return new DefaultCase(expression);
    }

    public static Symbol def(String name, Symbol expression, Symbol... annotations) {
        return new Declaration(name, expression, annotations);
    }

    public static Symbol embrace(String var, Symbol type, Symbol expression) {
        return new EmbraceCase(var, type, expression);
    }

    public static Symbol ensure(Symbol expression) {
        return new EnsureCase(expression);
    }

    public static Symbol entry(Symbol key, Symbol value) {
        return new MapEntry(key, value);
    }

    public static Symbol falsy(Symbol condition, Symbol expression) {
        return new FalsyCase(condition, expression);
    }

    public static Symbol from(String[] module, Symbol... subImports) {
        return new FromImport(module, subImports);
    }

    public static Symbol func(Symbol[] args, Symbol body, Symbol type) {
        return new FunctionLiteral(args, body, type);
    }

    public static Symbol id(String name) {
        return new Identifier(name);
    }

    public static Symbol importId(String[] id) {
        return new Import(id, id[id.length - 1]);
    }

    public static Symbol importId(String[] id, String alias) {
        return new Import(id, alias);
    }

    public static Symbol index(Symbol expression, Symbol... arguments) {
        return new IndexExpression(expression, arguments);
    }

    public static Symbol interpolation(Symbol... elements) {
        return new StringInterpolation(elements);
    }

    public static Symbol list(Symbol... elements) {
        return new ListLiteral(elements);
    }

    public static Symbol literal(String value) {
        return new StringLiteral(value);
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

    public static Symbol loop(Symbol body, Symbol defaultCase) {
        return new Loop(body, defaultCase);
    }

    public static Symbol loop(String var, Symbol elements, Symbol body, Symbol defaultCase) {
        return new IteratorLoop(var, elements, body, defaultCase);
    }

    public static Symbol map(Symbol... entries) {
        return new MapLiteral(entries);
    }

    public static Symbol module(Symbol... elements) {
        return new Module(elements);
    }

    public static Symbol nothing() {
        return new NothingLiteral();
    }

    public static Symbol regex(List<Symbol> elements, Set<Character> options) {
        return new RegexLiteral(elements, options);
    }

    public static Symbol set(Symbol element, Symbol[] elements) {
        return new SetLiteral(element, elements);
    }

    public static Symbol set(Symbol... elements) {
        return new SetLiteral(elements);
    }

    public static Symbol sub(String id) {
        return new SubImport(id, id);
    }

    public static Symbol sub(String id, String alias) {
        return new SubImport(id, alias);
    }

    public static Symbol symbol(String name) {
        return new SymbolLiteral(name);
    }

    public static Symbol truthy(Symbol condition, Symbol expression) {
        return new TruthyCase(condition, expression);
    }

    public static Symbol tuple(Symbol... elements) {
        return new TupleLiteral(elements);
    }

    public static Symbol type(String[] id) {
        return new TypeSpec(id);
    }

    public static Symbol unary(String operator, Symbol operand) {
        return new UnaryExpression(operator, operand);
    }

    public static Symbol using(String var, Symbol value) {
        return new Using(var, value);
    }

    public static Symbol using(Symbol value) {
        return new Using(null, value);
    }

    public static Symbol using(Symbol[] usings, Symbol expression) {
        return new Exceptional(usings, expression, null, null);
    }

    public static Symbol using(Symbol[] usings, Symbol expression, Symbol[] embraceCases) {
        return new Exceptional(usings, expression, embraceCases, null);
    }

    public static Symbol using(Symbol[] usings, Symbol expression, Symbol ensureCase) {
        return new Exceptional(usings, expression, null, ensureCase);
    }

    public static Symbol using(Symbol[] usings, Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        return new Exceptional(usings, expression, embraceCases, ensureCase);
    }

    private SyntaxFactory() {
        // intentionally empty
    }
}
