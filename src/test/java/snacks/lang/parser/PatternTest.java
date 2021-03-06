package snacks.lang.parser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import snacks.lang.Type;
import snacks.lang.ast.ClosureLocator;
import snacks.lang.ast.NamedNode;
import snacks.lang.ast.Reference;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static snacks.lang.Types.algebraic;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.integerType;
import static snacks.lang.Types.property;
import static snacks.lang.Types.record;
import static snacks.lang.Types.recur;
import static snacks.lang.Types.simple;
import static snacks.lang.Types.stringType;
import static snacks.lang.ast.AstFactory.access;
import static snacks.lang.ast.AstFactory.closure;
import static snacks.lang.ast.AstFactory.constant;
import static snacks.lang.ast.AstFactory.declaration;
import static snacks.lang.ast.AstFactory.func;
import static snacks.lang.ast.AstFactory.matchConstant;
import static snacks.lang.ast.AstFactory.matchConstructor;
import static snacks.lang.ast.AstFactory.nop;
import static snacks.lang.ast.AstFactory.pattern;
import static snacks.lang.ast.AstFactory.patterns;
import static snacks.lang.ast.AstFactory.reference;
import static snacks.lang.ast.AstFactory.var;
import static snacks.lang.parser.TranslatorMatcher.defines;

public class PatternTest extends AbstractTranslatorTest {

    @Rule
    public ExpectedException thrown = none();

    @Test
    public void shouldTranslateRecordPattern() {
        Type type = record("test.BreakfastItem", asList(
            property("name", stringType()),
            property("tasteIndex", integerType()),
            property("pairsWithBacon?", booleanType())
        ));
        String argName = "#snacks#~patternArg0";
        Reference argument = reference(vl(argName), type);
        Collection<NamedNode> nodes = translate(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? :: BreakfastItem -> Boolean",
            "bacon? = ?(BreakfastItem { pairsWithBacon? = x }) -> x"
        );
        assertThat(nodes, defines(declaration("test.bacon?", func(func(type, booleanType()), argName, patterns(
            booleanType(),
            asList(pattern(
                asList(matchConstructor(argument, asList(
                    var("x", access(argument, "pairsWithBacon?", booleanType()))
                ))),
                reference(vl("x"), booleanType())
            ))
        )))));
    }

    @Test
    public void shouldTranslatePatternWithTwoArguments() {
        Type treeType = algebraic("test.Tree", asList(
            simple("test.Leaf"),
            record("test.Node", asList(
                property("_0", integerType()),
                property("_1", recur("test.Tree")),
                property("_2", recur("test.Tree"))
            ))
        ));
        Type leafType = simple("test.Leaf");
        Collection<NamedNode> nodes = translate(
            "data Tree = Leaf | Node Integer Tree Tree",
            "leafs? :: Tree -> Tree -> Boolean",
            "leafs? = ?(Leaf, Leaf) -> True",
            "leafs? = ?(_, _) -> False"
        );
        assertThat(nodes, defines(declaration("test.leafs?", func(
            func(treeType, func(treeType, booleanType())),
            "#snacks#~patternArg0",
            reference(new ClosureLocator("test.leafs?_1", asList("#snacks#~pattern0")), func(treeType, booleanType()))
        ))));
        assertThat(nodes, defines(declaration("test.leafs?_1", closure(
            func(treeType, booleanType()),
            "#snacks#~patternArg1",
            patterns(booleanType(), asList(
                pattern(asList(
                    matchConstant(reference(vl("#snacks#~patternArg0"), leafType), ref(dl("test.Leaf"))),
                    matchConstant(reference(vl("#snacks#~patternArg1"), leafType), ref(dl("test.Leaf")))
                ), constant(true)),
                pattern(asList(nop(), nop()), constant(false))
            )),
            asList("#snacks#~patternArg0")
        ))));
    }

    @Test
    public void shouldTranslateAlgebraicPattern() {
        Type a = vtype("test.Maybe#a");
        Type nothing = simple("test.Nothing");
        Type just = record("test.Just", asList(a), asList(property("_0", a)));
        Type maybe = algebraic("test.Maybe", asList(a), asList(nothing, just));
        Collection<NamedNode> nodes = translate(
            "data Maybe a = Nothing | Just a",
            "optional :: Maybe a -> a -> a",
            "optional = ?(Nothing, x) -> x",
            "optional = ?(Just x, _) -> x"
        );
        assertThat(nodes, defines(declaration("test.optional", func(
            func(maybe, func(a, a)),
            "#snacks#~patternArg0",
            reference(new ClosureLocator("test.optional_1", asList("#snacks#~patternArg0")), func(a, a))
        ))));
        assertThat(nodes, defines(declaration("test.optional_1",closure(func(a, a), "#snacks#~patternArg1", patterns(a, asList(
            pattern(asList(
                matchConstant(reference(vl("#snacks#~patternArg0"), nothing), ref(dl("test.Nothing"))),
                var("x", reference(vl("#snacks#~patternArg1"), a))
            ), reference(vl("x"), a)),
            pattern(asList(
                matchConstructor(reference(vl("#snacks#~patternArg0"), just), asList(var("x", access(
                    reference(vl("#snacks#~patternArg0"), just),
                    "_0",
                    a
                )))),
                nop()
            ), reference(vl("x"), a))
        )), asList("#snacks#~patternArg0")))));
    }

    @Test
    public void shouldThrowExceptionIfPatternHasMoreArgumentsThanExpected() {
        thrown.expect(PatternException.class);
        thrown.expectMessage(containsString("accepts more arguments than type signature allows"));
        translate(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? :: BreakfastItem -> Maybe Boolean -> Boolean",
            "bacon? = ?(BreakfastItem { pairsWithBacon? = x }, Nothing) -> x",
            "bacon? = ?(BreakfastItem {}, Just x, Nothing) -> x"
        );
    }

    @Test
    public void shouldThrowExceptionIfPatternArgumentDoesNotMatchSignature() {
        thrown.expect(PatternException.class);
        thrown.expectMessage(containsString("argument(1) type does not match expected type"));
        translate(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? :: BreakfastItem -> Maybe Boolean -> Boolean",
            "bacon? = ?(BreakfastItem { pairsWithBacon? = x }, Nothing) -> x",
            "bacon? = ?(BreakfastItem {}, BreakfastItem { pairsWithBacon? = x }) -> x"
        );
    }

    @Test
    public void shouldThrowExceptionIfPatternReturnTypeDoesNotMatchSignature() {
        thrown.expect(PatternException.class);
        thrown.expectMessage(containsString("return type does not match expected return type"));
        translate(
            "data BreakfastItem = {",
            "    name: String,",
            "    tasteIndex: Integer,",
            "    pairsWithBacon?: Boolean,",
            "}",
            "bacon? :: BreakfastItem -> Maybe Boolean -> Boolean",
            "bacon? = ?(BreakfastItem { pairsWithBacon? = x }, Nothing) -> x",
            "bacon? = ?(BreakfastItem {}, Just x) -> 'oops'"
        );
    }
}
