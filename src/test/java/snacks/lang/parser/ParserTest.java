package snacks.lang.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static snacks.lang.parser.CompilerUtil.expression;
import static snacks.lang.parser.CompilerUtil.parse;
import static snacks.lang.parser.syntax.SyntaxFactory.*;

import beaver.Symbol;
import org.junit.Test;

public class ParserTest {

    @Test
    public void shouldParseTwoPlusTwo() {
        assertThat(expression("2 + 2"), equalTo(msg(literal(2), id("+"), literal(2))));
    }

    @Test
    public void shouldParseParenthetical() {
        assertThat(expression("(2 + 2)"), equalTo(msg(literal(2), id("+"), literal(2))));
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
    public void shouldParseIndexExpression() {
        assertThat(expression("fruits[0]"),
            equalTo(apply(apply(id("[]"), id("fruits")), literal(0))));
    }

    @Test
    public void shouldParseIndexExpressionWithMultipleArguments() {
        assertThat(expression("fruits[1 2 3]"),
            equalTo(apply(apply(apply(apply(id("[]"), id("fruits")), literal(1)), literal(2)), literal(3))));
    }

    @Test
    public void shouldParseArgumentsExpression() {
        assertThat(expression("fruits 0"), equalTo(msg(id("fruits"), literal(0))));
    }

    @Test
    public void shouldParseMultipleArgumentsExpression() {
        assertThat(expression("fruits 1 2 3"), equalTo(msg(id("fruits"), literal(1), literal(2), literal(3))));
    }

    @Test
    public void shouldParseParenthesizedArgumentsExpression() {
        assertThat(expression("fruits(1 2 3)"), equalTo(apply(apply(apply(id("fruits"), literal(1)), literal(2)), literal(3))));
    }

    @Test
    public void shouldParseFunction() {
        assertThat(expression("(a b c -> a b c)"), equalTo(
            func(arg("a"), func(arg("b"), func(arg("c"), msg(id("a"), id("b"), id("c")))))
        ));
    }

    @Test
    public void shouldParseFunctionWithNewLine() {
        Symbol tree = expression(
            "(a b c ->",
            "    a b c)"
        );
        assertThat(tree, equalTo(
            func(arg("a"), func(arg("b"), func(arg("c"), msg(id("a"), id("b"), id("c")))))
        ));
    }

    @Test
    public void shouldParseTailedFunction() {
        assertThat(expression("(a b c) -> a b c"), equalTo(
            func(arg("a"), func(arg("b"), func(arg("c"), msg(id("a"), id("b"), id("c")))))
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
        assertThat(tree, equalTo(
            func(arg("x"), func(arg("y"), func(arg("z"), block(
                msg(id("print"), id("x"), id("y")),
                result(id("z"))
            ))))
        ));
    }

    @Test
    public void shouldParseTypedFunction() {
        assertThat(expression("(a:x b:y -> a b)"), equalTo(func(
            arg("a", type(qid("x"))),
            func(
                arg("b", type(qid("y"))),
                msg(id("a"), id("b"))
            )
        )));
    }

    @Test
    public void shouldParseTypedTailedFunction() {
        assertThat(expression("(a:x b:y):z -> a b"), equalTo(func(
            arg("a", type(qid("x"))),
            func(
                arg("b", type(qid("y"))),
                msg(id("a"), id("b"))
            ),
            type(qid("z"))
        )));
    }

    @Test
    public void shouldParseFunctionWithNoArgs() {
        assertThat(expression("(-> a b)"), equalTo(invokable(msg(id("a"), id("b")))));
    }

    @Test
    public void shouldParseTailedFunctionWithNoArgs() {
        assertThat(expression("() -> a b"), equalTo(invokable(msg(id("a"), id("b")))));
    }

    @Test
    public void shouldParseTypedMultiLineFunction() {
        Symbol tree = expression(
            "{ x:a y:b ->",
            "    print x",
            "    return x y",
            "}"
        );
        assertThat(tree, equalTo(func(
            arg("x", type(qid("a"))),
            func(
                arg("y", type(qid("b"))),
                block(
                    msg(id("print"), id("x")),
                    result(msg(id("x"), id("y")))
                )
            )
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
        assertThat(expression("{}"), equalTo(invokable(block())));
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
            def("test", msg(id("a"), id("b"), id("c")))
        )));
    }

    @Test
    public void shouldParseFunctionCalledWithZeroArgs() {
        assertThat(expression("test()"), equalTo(invocation(id("test"))));
    }

    @Test
    public void shouldParseVariableDeclaration() {
        Symbol tree = parse(
            "test = {",
            "    var waffles = bananas",
            "    say waffles",
            "}"
        );
        assertThat(tree, equalTo(module(
            def("test", invokable(block(
                var("waffles", id("bananas")),
                msg(id("say"), id("waffles"))
            )))
        )));
    }

    @Test
    public void shouldParseConditionalCase() {
        Symbol tree = parse(
            "test = if bananas then",
            "           waffles 1 2 3",
            "       else if toast then",
            "           ducks 4 5 6",
            "       else",
            "           waffles anyway!",
            "       end"
        );
        assertThat(tree, equalTo(module(
            def("test", conditional(
                condition(id("bananas"), block(msg(id("waffles"), literal(1), literal(2), literal(3)))),
                condition(id("toast"), block(msg(id("ducks"), literal(4), literal(5), literal(6)))),
                block(msg(id("waffles"), id("anyway!")))
            ))
        )));
    }

    @Test
    public void shouldParseExceptionalCase() {
        Symbol tree = parse(
            "test = begin",
            "           try something dangerous",
            "       embrace oops ->",
            "           say 'oops, I broke it'",
            "       ensure",
            "           perform some cleanup",
            "       end"
        );
        assertThat(tree, equalTo(module(
            def("test", begin(
                block(msg(id("try"), id("something"), id("dangerous"))),
                array(embrace(
                    "oops",
                    type(qid("snacks", "lang", "SnacksException")),
                    block(msg(id("say"), literal("oops, I broke it")))
                )),
                block(msg(id("perform"), id("some"), id("cleanup")))
            ))
        )));
    }

    @Test
    public void shouldParseUseCase() {
        Symbol tree = parse(
            "test = use try = uranium238",
            "           try something dangerous",
            "       embrace oops ->",
            "           say 'oops, I broke it'",
            "       ensure",
            "           perform some cleanup",
            "       end"
        );
        assertThat(tree, equalTo(module(
            def("test", begin(
                array(
                    use("try", id("uranium238"))
                ),
                block(msg(id("try"), id("something"), id("dangerous"))),
                array(embrace(
                    "oops",
                    type(qid("snacks", "lang", "SnacksException")),
                    block(msg(id("say"), literal("oops, I broke it")))
                )),
                block(msg(id("perform"), id("some"), id("cleanup")))
            ))
        )));
    }

    @Test
    public void shouldParseMultipleUses() {
        Symbol tree = parse(
            "test = use try = uranium238",
            "       use something = centrifuge + lots of electricity",
            "           try something dangerous",
            "       embrace oops ->",
            "           say 'oops, boom!'",
            "       ensure",
            "           perform some cleanup",
            "       end"
        );
        assertThat(tree, equalTo(module(
            def("test", begin(
                array(
                    use("try", id("uranium238")),
                    use("something", msg(id("centrifuge"), id("+"), id("lots"), id("of"), id("electricity")))
                ),
                block(msg(id("try"), id("something"), id("dangerous"))),
                array(embrace(
                    "oops",
                    type(qid("snacks", "lang", "SnacksException")),
                    block(msg(id("say"), literal("oops, boom!")))
                )),
                block(msg(id("perform"), id("some"), id("cleanup")))
            ))
        )));
    }

    @Test
    public void shouldParseExceptionalWithTypedEmbrace() {
        Symbol tree = parse(
            "test = begin",
            "           try something dangerous",
            "       embrace oops:ouch.BustedFoot ->",
            "           say 'ouch!'",
            "       ensure",
            "           perform some cleanup",
            "       end"
        );
        assertThat(tree, equalTo(module(
            def("test", begin(
                block(msg(id("try"), id("something"), id("dangerous"))),
                array(
                    embrace("oops", type(qid("ouch", "BustedFoot")), block(msg(id("say"), literal("ouch!"))))
                ),
                block(msg(id("perform"), id("some"), id("cleanup")))
            ))
        )));
    }

    @Test
    public void shouldParseUndeclaredUse() {
        Symbol tree = parse(
            "test = use 'secret' surveillance program",
            "           try something unconstitutional",
            "       embrace problem:PoliticalFallout ->",
            "           make problem disappear",
            "       end"
        );
        assertThat(tree, equalTo(module(
            def("test", begin(
                array(
                    using(msg(literal("secret"), id("surveillance"), id("program")))
                ),
                block(msg(id("try"), id("something"), id("unconstitutional"))),
                array(embrace("problem",
                    type(qid("PoliticalFallout")),
                    block(msg(id("make"), id("problem"), id("disappear")))
                ))
            ))
        )));
    }

    @Test
    public void shouldParsePartialApplication() {
        assertThat(parse("example = `+` 2"), equalTo(module(
            def("example", msg(quoted("+"), literal(2)))
        )));
    }

    @Test
    public void shouldParseWildcardImport() {
        assertThat(parse("import example.monkey._"), equalTo(module(
            importWildcard(qid("example", "monkey"))
        )));
    }

    @Test
    public void shouldParseOperatorImport() {
        assertThat(parse("import example.funny.`..`"), equalTo(module(
            importId(qid("example", "funny", ".."))
        )));
    }

    @Test
    public void shouldParseDeclarationSignature() {
        assertThat(parse("addIntegers :: Integer -> Integer -> Integer"), equalTo(module(
            sig("addIntegers", fsig(type("Integer"), fsig(type("Integer"), type("Integer"))))
        )));
    }

    @Test
    public void shouldParseDeclarationSignatureWithTuple() {
        assertThat(parse("something :: (String, Boolean, Integer) -> ()"), equalTo(module(
            sig("something", fsig(tsig(type("String"), type("Boolean"), type("Integer")), type(qid("snacks", "lang", "Void"))))
        )));
    }

    @Test
    public void shouldParseDeclarationReturningTuple() {
        assertThat(parse("pair :: String -> Integer -> (String, Integer)"), equalTo(module(
            sig("pair", fsig(type("String"), fsig(type("Integer"), tsig(type("String"), type("Integer")))))
        )));
    }

    @Test
    public void shouldParseOperatorSpec() {
        Symbol tree = parse(
            "** infix left 0",
            ">>= infix right 8",
            ".. infix none 7"
        );
        assertThat(tree, equalTo(module(
            leftOp("**", 0),
            rightOp(">>=", 8),
            op("..", 7)
        )));
    }

    @Test
    public void shouldCreateLoopFromPostfixWhile() {
        Symbol tree = parse(
            "main = {",
            "    var x = 0",
            "    x = x + 1, while x < 10",
            "}"
        );
        assertThat(tree, equalTo(module(
            def("main", invokable(block(
                var("x", literal(0)),
                loop(
                    msg(id("x"), id("<"), literal(10)),
                    assign(id("x"), msg(id("x"), id("+"), literal(1)))
                )
            )))
        )));
    }

    @Test
    public void shouldParseRightHandOfAssignmentAsOneMsg() {
        Symbol tree = parse(
            "main = { x = x + 1 }"
        );
        assertThat(tree, equalTo(module(
            def("main", invokable(block(assign(id("x"), msg(id("x"), id("+"), literal(1))))))
        )));
    }
}
