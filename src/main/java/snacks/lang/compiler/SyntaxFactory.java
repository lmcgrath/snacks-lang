package snacks.lang.compiler;

import static java.util.Arrays.asList;
import static java.util.Collections.addAll;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import beaver.Symbol;
import snacks.lang.compiler.syntax.*;

public final class SyntaxFactory {

    public static Symbol access(Symbol expression, String property) {
        return new AccessExpression(expression, property);
    }

    public static Symbol annotated(Symbol expression, Symbol... annotations) {
        return new Annotated(expression, annotations);
    }

    public static Symbol annotation(Symbol id, Symbol value) {
        return new Annotation(id, value);
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

    public static Symbol begin(Symbol expression, Symbol[] embraceCases) {
        return new Exceptional(null, expression, embraceCases, null);
    }

    public static Symbol begin(Symbol expression, Symbol ensureCase) {
        return new Exceptional(null, expression, null, ensureCase);
    }

    public static Symbol begin(Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        return new Exceptional(null, expression, embraceCases, ensureCase);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression) {
        return new Exceptional(usings, expression, null, null);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression, Symbol[] embraceCases) {
        return new Exceptional(usings, expression, embraceCases, null);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression, Symbol ensureCase) {
        return new Exceptional(usings, expression, null, ensureCase);
    }

    public static Symbol begin(Symbol[] usings, Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        return new Exceptional(usings, expression, embraceCases, ensureCase);
    }

    public static Symbol binary(String operator, Symbol left, Symbol right) {
        return apply(apply(id(operator), left), right);
    }

    public static Symbol block(Symbol... elements) {
        return new Block(elements);
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
        if (defaultCase != null) {
            elements.add(defaultCase);
        }
        return new Conditional(elements);
    }

    public static Symbol dcase(Symbol expression) {
        return new DefaultCase(expression);
    }

    public static Symbol def(String name, Symbol expression) {
        return new Declaration(name, expression);
    }

    public static Symbol embrace(String var, Symbol expression) {
        return new EmbraceCase(var, null, expression);
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

    public static Symbol from(Symbol module, Symbol... subImports) {
        return new FromImport(module, subImports);
    }

    public static Symbol func(Symbol arg, Symbol body) {
        return new FunctionLiteral(arg, body, null);
    }

    public static Symbol func(Symbol arg, Symbol body, Symbol type) {
        return new FunctionLiteral(arg, body, type);
    }

    public static Symbol hurl(Symbol expression) {
        return new Hurl(expression);
    }

    public static Symbol id(String name) {
        return new Identifier(name);
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

    public static Symbol index(Symbol expression, Symbol... arguments) {
        return new IndexExpression(expression, arguments);
    }

    public static Symbol invokable(Symbol body) {
        return new InvokableLiteral(body);
    }

    public static Symbol invocation(Symbol function) {
        return new Invocation(function);
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

    public static Symbol qid(QualifiedIdentifier id, String segment) {
        return new QualifiedIdentifier(id, segment);
    }

    public static Symbol qid(String... ids) {
        return new QualifiedIdentifier(ids);
    }

    public static Symbol regex(List<Symbol> elements, Set<Character> options) {
        return new RegexLiteral(elements, options);
    }

    public static Symbol result(Symbol expression) {
        return new Result(expression);
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

    public static Symbol tuple(Symbol element, Symbol[] elements) {
        return new TupleLiteral(element, elements);
    }

    public static Symbol type(Symbol id) {
        return new TypeSpec(id);
    }

    public static Symbol unary(String operator, Symbol operand) {
        return apply(id(operator), operand);
    }

    public static Symbol use(String var, Symbol value) {
        return new Using(var, value);
    }

    public static Symbol using(Symbol value) {
        return new Using(null, value);
    }

    public static Symbol var(String name, Symbol value) {
        return new Var(name, value);
    }

    private SyntaxFactory() {
        // intentionally empty
    }
}
