package snacks.lang.compiler;

import static org.apache.commons.lang.StringUtils.join;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.compiler.SyntaxFactory.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import beaver.Symbol;
import org.junit.Test;
import snacks.lang.compiler.Parser.AltGoals;

public class ParserTest {

    @Test
    public void shouldParseTwoPlusTwo() {
        assertThat(expression("2 + 2"), equalTo(binary("+", literal(2), literal(2))));
    }

    @Test
    public void shouldParseParenthetical() {
        assertThat(expression("(2 + 2)"), equalTo(binary("+", literal(2), literal(2))));
    }

    @Test
    public void shouldParseTwoPlusTwoWithNewLine() {
        Symbol tree = expression(
            "2 +",
            "2"
        );
        assertThat(tree, equalTo(binary("+", literal(2), literal(2))));
    }

    @Test
    public void shouldParseTuple() {
        assertThat(expression("(a, b)"), equalTo(tuple(id("a"), id("b"))));
    }

    @Test
    public void shouldParseEmptyTuple() {
        assertThat(expression("()"), equalTo(tuple()));
        assertThat(expression("(,)"), equalTo(tuple()));
    }

    @Test
    public void shouldParseList() {
        assertThat(expression("[a]"), equalTo(list(id("a"))));
        assertThat(expression("[a, b,]"), equalTo(list(id("a"), id("b"))));
        assertThat(expression("[a, b, c]"), equalTo(list(id("a"), id("b"), id("c"))));
    }

    @Test
    public void shouldParseSet() {
        assertThat(expression("{a,}"), equalTo(set(id("a"))));
        assertThat(expression("{a, b, c}"), equalTo(set(id("a"), id("b"), id("c"))));
    }

    @Test
    public void shouldParseEmptySet() {
        assertThat(expression("{,}"), equalTo(set()));
    }

    @Test
    public void shouldParseKeyedMap() {
        assertThat(expression("{ one: 1, two: 2 }"), equalTo(map(
            entry(symbol("one"), literal(1)),
            entry(symbol("two"), literal(2))
        )));
    }

    @Test
    public void shouldParseMap() {
        assertThat(expression("{ 'one' => 1, 'two' => 2 }"), equalTo(map(
            entry(literal("one"), literal(1)),
            entry(literal("two"), literal(2))
        )));
    }

    @Test
    public void shouldParseEmptyMap() {
        assertThat(expression("{:}"), equalTo(map()));
    }

    @Test
    public void shouldParseFunction() {
        assertThat(expression("(a b c -> a b c)"), equalTo(
            func(array(arg("a"), arg("b"), arg("c")), apply(id("a"), id("b"), id("c")))
        ));
    }

    @Test
    public void shouldParseFunctionWithNewLine() {
        Symbol tree = expression(
            "(a b c ->",
            "    a b c)"
        );
        assertThat(tree, equalTo(
            func(array(arg("a"), arg("b"), arg("c")), apply(id("a"), id("b"), id("c")))
        ));
    }

    @Test
    public void shouldParseTailedFunction() {
        assertThat(expression("(a b c) -> a b c"), equalTo(
            func(array(arg("a"), arg("b"), arg("c")), apply(id("a"), id("b"), id("c")))
        ));
    }

    @Test
    public void shouldParseMultiLineFunction() {
        Symbol tree = expression(
            "{ x y z ->",
            "    print x y",
            "    return z",
            "}"
        );
        assertThat(tree, equalTo(func(
            array(arg("x"), arg("y"), arg("z")),
            block(
                apply(id("print"), id("x"), id("y")),
                result(id("z"))
            )
        )));
    }

    @Test
    public void shouldParseTypedFunction() {
        assertThat(expression("(a:x b:y :: z -> a b)"), equalTo(func(
            array(arg("a", type(qid("x"))), arg("b", type(qid("y")))),
            apply(id("a"), id("b")),
            type(qid("z"))
        )));
    }

    @Test
    public void shouldParseTypedTailedFunction() {
        assertThat(expression("(a:x b:y):z -> a b"), equalTo(func(
            array(arg("a", type(qid("x"))), arg("b", type(qid("y")))),
            apply(id("a"), id("b")),
            type(qid("z"))
        )));
    }

    @Test
    public void shouldParseTypedMultiLineFunction() {
        Symbol tree = expression(
            "{ x:a y:b :: z ->",
            "    print x",
            "    return x y",
            "}"
        );
        assertThat(tree, equalTo(func(
            array(arg("x", type(qid("a"))), arg("y", type(qid("b")))),
            block(
                apply(id("print"), id("x")),
                result(apply(id("x"), id("y")))
            ),
            type(qid("z"))
        )));
    }

    @Test
    public void shouldParseMultiLineMap() {
        Symbol tree = expression(
            "{",
            "    one: 1,",
            "    two: 2,",
            "    three: 3,",
            "}"
        );
        assertThat(tree, equalTo(map(
            entry(symbol("one"), literal(1)),
            entry(symbol("two"), literal(2)),
            entry(symbol("three"), literal(3))
        )));
    }

    @Test
    public void shouldParseEmptyBlock() {
        assertThat(expression("{}"), equalTo(block()));
    }

    @Test
    public void shouldParseImport() {
        assertThat(parse("import snacks.bananas"), equalTo(module(
            importId(qid("snacks", "bananas")))
        ));
    }

    @Test
    public void shouldParseImportWithAlias() {
        assertThat(parse("import snacks.bananas as fruit"), equalTo(module(
            importId(qid("snacks", "bananas"), "fruit")
        )));
    }

    @Test
    public void shouldParseFrom() {
        assertThat(parse("from snacks.fruit import bananas"), equalTo(module(
            from(qid("snacks", "fruit"), sub("bananas"))
        )));
    }

    @Test
    public void shouldParseFromWithAlias() {
        assertThat(parse("from snacks.fruit import bananas as fruit"), equalTo(module(
            from(qid("snacks", "fruit"), sub("bananas", "fruit"))
        )));
    }

    @Test
    public void shouldParseMultipleFrom() {
        assertThat(parse("from snacks.fruit import bananas, apples, pears"), equalTo(module(
            from(
                qid("snacks", "fruit"),
                sub("bananas"),
                sub("apples"),
                sub("pears")
            )
        )));
    }

    @Test
    public void shouldParseMultipleFromWithParentheses() {
        assertThat(parse("from snacks.fruit import (bananas, apples, pears)"), equalTo(module(
            from(
                qid("snacks", "fruit"),
                sub("bananas"),
                sub("apples"),
                sub("pears")
            )
        )));
    }

    @Test
    public void shouldParseMultipleFromWithAlias() {
        assertThat(parse("from snacks.fruit import bananas, apples, pears as pandas"), equalTo(module(
            from(
                qid("snacks", "fruit"),
                sub("bananas"),
                sub("apples"),
                sub("pears", "pandas")
            )
        )));
    }

    @Test
    public void shouldParseDeclaration() {
        assertThat(parse("test = a b c"), equalTo(module(
            def("test", apply(id("a"), id("b"), id("c"))))
        ));
    }

    @Test
    public void shouldParseDeclarationType() {
        Symbol tree = parse(
            "test :: waffles",
            "test = a b c"
        );
        assertThat(tree, equalTo(module(
            defType("test", type(qid("waffles"))),
            def("test", apply(id("a"), id("b"), id("c")))
        )));
    }

    private static Symbol expression(String... inputs) {
        try {
            return (Symbol) new Parser().parse(
                new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8")))),
                AltGoals.expression
            );
        } catch (IOException | Parser.Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Symbol parse(String... inputs) {
        try {
            return (Symbol) new Parser().parse(
                new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8"))))
            );
        } catch (IOException | Parser.Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
