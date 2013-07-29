package iddic.lang.compiler;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static iddic.lang.compiler.SyntaxFactory.*;

import java.util.HashSet;
import java.util.Set;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import iddic.lang.IddicException;
import iddic.lang.compiler.syntax.ModuleDeclaration;
import iddic.lang.compiler.syntax.VoidExpression;

public class IddicParserTest {

    @Test
    public void shouldParseImportExpression() throws IddicException {
        assertThat(parse("import iddic.lang.say"), equalTo(module(
            imports(qualifiedId("iddic", "lang", "say"), alias("say"))
        )));
    }

    @Test
    public void shouldParseFromExpression() throws IddicException {
        assertThat(parse("from iddic.io import write"), equalTo(module(
            from(qualifiedId("iddic", "io"), sub("write", alias("write")))
        )));
    }

    @Test
    public void shouldParseDeclaration() throws IddicException {
        assertThat(parse("test = 2 + 2"), equalTo(module(
            declaration("test", binary("+", literal(2), literal(2)))
        )));
    }

    @Test
    public void shouldParseEmptyInput() throws IddicException {
        assertThat(parse(""), equalTo(module()));
    }

    @Test
    public void shouldParseArgumentsExpression() throws IddicException {
        assertThat(parse("main = apples 1 2 3"), equalTo(module(
            declaration("main", select(id("apples"), args(literal(1), literal(2), literal(3))))
        )));
    }

    @Test
    public void shouldParseBooleanFalse() throws IddicException {
        assertThat(parse("main = False"), equalTo(module(
            declaration("main", literal(false))
        )));
    }

    @Test
    public void shouldParseBooleanTrue() throws IddicException {
        assertThat(parse("main = True"), equalTo(module(
            declaration("main", literal(true))
        )));
    }

    @Test
    public void shouldParseIdentifier() throws IddicException {
        assertThat(parse("main = apples"), equalTo(module(
            declaration("main", id("apples"))
        )));
    }

    @Test
    public void shouldParseFunctionWithArgumentParenthesesAsArgument() throws IddicException {
        assertThat(parse("main = 2.times (x) -> say x"), equalTo(module(
            declaration("main", select(select(literal(2), access("times")),
                args(func(args("x"), select(id("say"), args(id("x")))))))
        )));
    }

    @Test
    public void shouldParseFunctionAsArgument() throws IddicException {
        assertThat(parse("main = 2.times (x -> say x)"), equalTo(module(
            declaration("main", select(select(literal(2), access("times")),
                args(func("x", select(id("say"), args(id("x")))))))
        )));
    }

    @Test
    public void shouldParseDeclarationsOnTwoLines() throws IddicException {
        assertThat(parse("apples = 1", "oranges = 2"), equalTo(module(
            declaration("apples", literal(1)),
            declaration("oranges", literal(2))
        )));
    }

    @Test
    public void shouldParseMultiArgumentFunction() throws IddicException {
        assertThat(parse("y = (m x b -> m * x + b)"), equalTo(module(
            declaration("y", func(args("m", "x", "b"), binary("+", binary("*", id("m"), id("x")), id("b"))))
        )));
    }

    @Test
    public void shouldParseMultiArgumentFunctionWithArgumentParentheses() throws IddicException {
        assertThat(parse("y = (m x b) -> m * x + b"), equalTo(module(
            declaration("y", func(args("m", "x", "b"), binary("+", binary("*", id("m"), id("x")), id("b"))))
        )));
    }

    @Test
    public void shouldParseSingleArgumentFunction() throws IddicException {
        assertThat(parse("y = (x -> m * x + b)"), equalTo(module(
            declaration("y", func("x", binary("+", binary("*", id("m"), id("x")), id("b"))))
        )));
    }

    @Test
    public void shouldParseSingleArgumentFunctionWithArgumentParentheses() throws IddicException {
        assertThat(parse("y = (x) -> m * x + b"), equalTo(module(
            declaration("y", func("x", binary("+", binary("*", id("m"), id("x")), id("b"))))
        )));
    }

    @Test
    public void shouldParseConstantExpression() throws IddicException {
        assertThat(parse("y = m * x + b"), equalTo(module(
            declaration("y", binary("+", binary("*", id("m"), id("x")), id("b")))
        )));
    }

    @Test
    public void shouldParseExpressionWithoutArgumentsAndParentheticalBody() throws IddicException {
        assertThat(parse("y = (m * x + b)"), equalTo(module(
            declaration("y", binary("+", binary("*", id("m"), id("x")), id("b")))
        )));
    }

    @Test
    public void shouldParseParentheticalExpressionAsArgument() throws IddicException {
        assertThat(parse("main = 2.times (oranges + 1)"), equalTo(module(
            declaration("main", select(select(literal(2), access("times")), args(binary("+", id("oranges"), literal(1)))))
        )));
    }

    @Test
    public void shouldParseSingleIdentifier() throws IddicException {
        assertThat(parse("main = bananas"), equalTo(module(
            declaration("main", id("bananas"))
        )));
    }

    @Test
    public void shouldParseSingleInteger() throws IddicException {
        assertThat(parse("main = 2"), equalTo(module(
            declaration("main", literal(2))
        )));
    }

    @Test
    public void shouldParseTwoPlusTwo() throws IddicException {
        assertThat(parse("main = 2 + 2"), equalTo(module(
            declaration("main", binary("+", literal(2), literal(2)))
        )));
    }

    @Test
    public void shouldParseSayExpression() throws IddicException {
        assertThat(parse("main = say $ 2 + 2"), equalTo(module(
            declaration("main", binary("$", id("say"), binary("+", literal(2), literal(2))))
        )));
    }

    @Test
    public void shouldParseImportExpressions() throws IddicException {
        ModuleDeclaration module = parse(
            "import io.write",
            "main = write 'test.txt' 'Hello, World!'"
        );
        assertThat(module, equalTo(module(
            imports(qualifiedId("io", "write"), alias("write")),
            declaration("main", select(id("write"), args(literal("test.txt"), literal("Hello, World!"))))
        )));
    }

    @Test
    public void shouldParseMultipleImportExpressions() throws IddicException {
        ModuleDeclaration module = parse(
            "import io.write",
            "import std.Error",
            "main = write 'log' Error.last"
        );
        assertThat(module, equalTo(module(
            imports(qualifiedId("io", "write"), alias("write")),
            imports(qualifiedId("std", "Error"), alias("Error")),
            declaration("main", select(id("write"), args(literal("log"), select(id("Error"), access("last")))))
        )));
    }

    @Test
    public void shouldParseFromExpressionWithMultipleImports() throws IddicException {
        ModuleDeclaration module = parse(
            "from iddic.io import read, write",
            "main = write 'output.txt' $ read 'input.txt'"
        );
        assertThat(module, equalTo(module(
            from(qualifiedId("iddic", "io"), sub("read", alias("read")), sub("write", alias("write"))),
            declaration("main", binary(
                "$",
                select(id("write"), args(literal("output.txt"))),
                select(id("read"), args(literal("input.txt")))
            ))
        )));
    }

    @Test
    public void shouldParseFromExpressionWithParentheses() throws IddicException {
        ModuleDeclaration module = parse(
            "from iddic.io import (read, write,)",
            "main = write 'output.txt' $ read 'input.txt'"
        );
        assertThat(module, equalTo(module(
            from(qualifiedId("iddic", "io"), sub("read", alias("read")), sub("write", alias("write"))),
            declaration("main", binary(
                "$",
                select(id("write"), args(literal("output.txt"))),
                select(id("read"), args(literal("input.txt")))
            ))
        )));
    }

    @Test
    public void shouldParseFromExpressionWithAliases() throws IddicException {
        ModuleDeclaration module = parse(
            "from iddic.io import read as input, write as output",
            "main = output 'output.txt' $ input 'input.txt'"
        );
        assertThat(module, equalTo(module(
            from(qualifiedId("iddic", "io"), sub("read", alias("input")), sub("write", alias("output"))),
            declaration("main", binary(
                "$",
                select(id("output"), args(literal("output.txt"))),
                select(id("input"), args(literal("input.txt")))
            ))
        )));
    }

    @Test
    public void shouldParseIsNothingExpression() throws IddicException {
        assertThat(parse("test = 2 is Nothing"), equalTo(module(
            declaration("test", binary("is", literal(2), nothing()))
        )));
    }

    @Test
    public void shouldParseIsNotNothingExpression() throws IddicException {
        assertThat(parse("test = 2 is not Nothing"), equalTo(module(
            declaration("test", binary("is not", literal(2), nothing()))
        )));
    }

    @Test
    public void shouldParseEmptyListLiteral() throws IddicException {
        assertThat(parse("test = []"), equalTo(module(
            declaration("test", list())
        )));
    }

    @Test
    public void shouldParseListLiteralWithOneElement() throws IddicException {
        assertThat(parse("test = [1]"), equalTo(module(
            declaration("test", list(literal(1)))
        )));
    }

    @Test
    public void shouldParseListLiteralWithOneElementAndTrailingComma() throws IddicException {
        assertThat(parse("test = [1,]"), equalTo(module(
            declaration("test", list(literal(1)))
        )));
    }

    @Test
    public void shouldParseListLiteralWithTwoElements() throws IddicException {
        assertThat(parse("test = [1, 2]"), equalTo(module(
            declaration("test", list(literal(1), literal(2)))
        )));
    }

    @Test
    public void shouldParseListLiteralWithTrailingComma() throws IddicException {
        assertThat(parse("test = [1, 2,]"), equalTo(module(
            declaration("test", list(literal(1), literal(2)))
        )));
    }

    @Test
    public void shouldParseListOfLists() throws IddicException {
        assertThat(parse("test = [[1, 2], [3, 4,]]"), equalTo(module(
            declaration("test", list(list(literal(1), literal(2)), list(literal(3), literal(4))))
        )));
    }

    @Test
    public void shouldParseListAsArgument() throws IddicException {
        assertThat(parse("test = apples [1, 2]"), equalTo(module(
            declaration("test", select(id("apples"), args(list(literal(1), literal(2)))))
        )));
    }

    @Test
    public void shouldParseEmptyTuple() throws IddicException {
        assertThat(parse("test = ()"), equalTo(module(
            declaration("test", tuple())
        )));
    }

    @Test
    public void shouldParseTupleLiteralWithOneElement() throws IddicException {
        assertThat(parse("test = (1,)"), equalTo(module(
            declaration("test", tuple(literal(1)))
        )));
    }

    @Test
    public void shouldParseTupleLiteralWithTwoElements() throws IddicException {
        assertThat(parse("test = (1, 2)"), equalTo(module(
            declaration("test", tuple(literal(1), literal(2)))
        )));
    }

    @Test
    public void shouldParseTupleLiteralWithTrailingComma() throws IddicException {
        assertThat(parse("test = (1, 2,)"), equalTo(module(
            declaration("test", tuple(literal(1), literal(2)))
        )));
    }

    @Test
    public void shouldParseTupleOfTuples() throws IddicException {
        assertThat(parse("test = ((1, 2), (3, 4,))"), equalTo(module(
            declaration("test", tuple(tuple(literal(1), literal(2)), tuple(literal(3), literal(4))))
        )));
    }

    @Test
    public void shouldParseTupleAsArgument() throws IddicException {
        assertThat(parse("test = apples (1, 2)"), equalTo(module(
            declaration("test", select(id("apples"), args(tuple(literal(1), literal(2)))))
        )));
    }

    @Test
    public void shouldParseSetLiteralWithOneElement() throws IddicException {
        assertThat(parse("test = {1,}"), equalTo(module(
            declaration("test", set(literal(1)))
        )));
    }

    @Test
    public void shouldParseSetLiteralWithTwoElements() throws IddicException {
        assertThat(parse("test = {1, 2}"), equalTo(module(
            declaration("test", set(literal(1), literal(2)))
        )));
    }

    @Test
    public void shouldParseSetLiteralWithTrailingComma() throws IddicException {
        assertThat(parse("test = {1, 2,}"), equalTo(module(
            declaration("test", set(literal(1), literal(2)))
        )));
    }

    @Test
    public void shouldParseSetOfSets() throws IddicException {
        assertThat(parse("test = {{1, 2}, {3, 4,}}"), equalTo(module(
            declaration("test", set(set(literal(1), literal(2)), set(literal(3), literal(4))))
        )));
    }

    @Test
    public void shouldParseSetAsArgument() throws IddicException {
        assertThat(parse("test = apples {1, 2}"), equalTo(module(
            declaration("test", select(id("apples"), args(set(literal(1), literal(2)))))
        )));
    }

    @Test
    public void shouldParseSymbol() throws IddicException {
        assertThat(parse("test = :symbol"), equalTo(module(
            declaration("test", symbol("symbol"))
        )));
    }

    @Test
    public void shouldParseSymbolAsArgument() throws IddicException {
        assertThat(parse("test = say :symbol"), equalTo(module(
            declaration("test", select(id("say"), args(symbol("symbol"))))
        )));
    }

    @Test
    public void shouldParseEmptyMapLiteral() throws IddicException {
        assertThat(parse("test = {:}"), equalTo(module(
            declaration("test", map())
        )));
    }

    @Test
    public void shouldParseEmptySetLiteral() throws IddicException {
        assertThat(parse("test = {,}"), equalTo(module(
            declaration("test", set())
        )));
    }

    @Test
    public void shouldParseLiteralMapWithSymbolKeys() throws IddicException {
        assertThat(parse("test = { one: 1, two: 2, +: 'plus', []: 'index' }"), equalTo(module(
            declaration("test", map(
                entry(symbol("one"), literal(1)),
                entry(symbol("two"), literal(2)),
                entry(symbol("+"), literal("plus")),
                entry(symbol("[]"), literal("index"))
            ))
        )));
    }

    @Test
    public void shouldParseLiteralMapWithSymbolLiteralKeys() throws IddicException {
        assertThat(parse("test = { :[] => 'index', :one => 1, :two => 2, :+ => 'plus' }"), equalTo(module(
            declaration("test", map(
                entry(symbol("[]"), literal("index")),
                entry(symbol("one"), literal(1)),
                entry(symbol("two"), literal(2)),
                entry(symbol("+"), literal("plus"))
            ))
        )));
    }

    @Test
    public void shouldParseLiteralMapWithStringKeys() throws IddicException {
        assertThat(parse("test = { 'one' => 1, 'two' => 2, 'three' => 3 }"), equalTo(module(
            declaration("test", map(
                entry(literal("one"), literal(1)),
                entry(literal("two"), literal(2)),
                entry(literal("three"), literal(3))
            ))
        )));
    }

    @Test
    public void shouldParseLiteralMapWithExpressionKeys() throws IddicException {
        assertThat(parse("test = { -1 + 2 => 'one', 6 / 3 => 'two', 4 - 1 => 'three' }"), equalTo(module(
            declaration("test", map(
                entry(binary("+", unary("-", literal(1)), literal(2)), literal("one")),
                entry(binary("/", literal(6), literal(3)), literal("two")),
                entry(binary("-", literal(4), literal(1)), literal("three"))
            ))
        )));
    }

    @Test
    public void shouldParseLiteralMapAsArgument() throws IddicException {
        assertThat(parse("test = apples { :one => 2, }"), equalTo(module(
            declaration("test", select(id("apples"), args(map(
                entry(symbol("one"), literal(2))
            ))))
        )));
    }

    @Test
    public void shouldParseExponent() throws IddicException {
        assertThat(parse("test = pi ** 2"), equalTo(module(
            declaration("test", binary("**", id("pi"), literal(2)))
        )));
    }

    @Test
    public void shouldParseExponentWithNegative() throws IddicException {
        assertThat(parse("test = pi ** -2"), equalTo(module(
            declaration("test", binary("**", id("pi"), unary("-", literal(2))))
        )));
    }

    @Test
    public void shouldParseImportExpressionAfterDeclaration() throws IddicException {
        ModuleDeclaration module = parse(
            "test = say 'Hello, World!'",
            "import iddic.say"
        );
        assertThat(module, equalTo(module(
            declaration("test", select(id("say"), args(literal("Hello, World!")))),
            imports(qualifiedId("iddic", "say"), alias("say"))
        )));
    }

    @Test
    public void shouldParseImportDeclarationAndFromExpressions() throws IddicException {
        ModuleDeclaration module = parse(
            "import iddic.say",
            "test = say 'Hello, World!'",
            "from some.module import more as stuff"
        );
        assertThat(module, equalTo(module(
            imports(qualifiedId("iddic", "say"), alias("say")),
            declaration("test", select(id("say"), args(literal("Hello, World!")))),
            from(qualifiedId("some", "module"), sub("more", alias("stuff")))
        )));
    }

    @Test
    public void shouldRetainSpaceBetweenInterpolatedExpressions() throws IddicException {
        assertThat(parse("test = \"#{element} #{element}\""), equalTo(module(
            declaration("test", interpolation(id("element"), literal(" "), id("element")))
        )));
    }

    @Test
    public void shouldParseDeclaredBlockExpression() throws IddicException {
        ModuleDeclaration module = parse(
            "test = {",
            "    apples",
            "    oranges",
            "}"
        );
        assertThat(module, equalTo(module(
            declaration("test", block(
                id("apples"),
                id("oranges")
            ))
        )));
    }

    @Test
    public void shouldParseBlockExpressionInMap() throws IddicException {
        ModuleDeclaration module = parse(
            "test = {",
            "    key: { do; something; }",
            "}"
        );
        assertThat(module, equalTo(module(
            declaration("test", map(
                entry(symbol("key"), block(
                    id("do"),
                    id("something")
                ))
            ))
        )));
    }

    @Test
    public void shouldParseBlockExpressionAsArgument() throws IddicException {
        assertThat(parse("test = apples { bananas; oranges; }"), equalTo(module(
            declaration("test", select(id("apples"), args(block(id("bananas"), id("oranges")))))
        )));
    }

    @Test
    public void shouldParseEqualityExpression() throws IddicException {
        assertThat(parse("test = apples != oranges"), equalTo(module(
            declaration("test", binary("!=", id("apples"), id("oranges")))
        )));
    }

    @Test
    public void shouldParseVariableDeclaration() throws IddicException {
        assertThat(parse("test = { var apples = oranges }"), equalTo(module(
            declaration("test", block(var("apples", id("oranges"))))
        )));
    }

    @Test
    public void shouldParseIndexerOnArgument() throws IddicException {
        assertThat(parse("test = sizeOf apples[1]"), equalTo(module(
            declaration("test", select(id("sizeOf"), args(select(id("apples"), index(literal(1))))))
        )));
    }

    @Test
    public void shouldParseAccessorOnArgument() throws IddicException {
        assertThat(parse("test = nameOf apple.color"), equalTo(module(
            declaration("test", select(id("nameOf"), args(select(id("apple"), access("color")))))
        )));
    }

    @Test
    public void shouldParseAccessorWithIndexerOnArgument() throws IddicException {
        assertThat(parse("test = nameOf apple.pests[0]"), equalTo(module(
            declaration("test", select(id("nameOf"), args(select(select(id("apple"), access("pests")), index(literal(0))))))
        )));
    }

    @Test
    public void shouldParseIndexerWithAccessorOnArgument() throws IddicException {
        assertThat(parse("test = sizeOf apples[1].pests"), equalTo(module(
            declaration("test", select(id("sizeOf"), args(select(select(id("apples"), index(literal(1))), access("pests")))))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithNoValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@empty",
            "test = 'I\\'m annotated with an empty meta :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with an empty meta :)"), meta("empty", nothing()))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithSymbolValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@symbol :value",
            "test = 'I\\'m annotated with a symbol :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a symbol :)"), meta("symbol", symbol("value")))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithBooleanValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@boolean True",
            "test = 'I\\'m annotated with a boolean :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a boolean :)"), meta("boolean", literal(true)))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithNullValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@blank Nothing",
            "test = 'I\\'m annotated with nothing :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with nothing :)"), meta("blank", nothing()))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithBlockValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@block { do something }",
            "test = 'I\\'m annotated with a block :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a block :)"),
                meta("block", block(select(id("do"), args(id("something"))))))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithFunctionValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@function (x y -> x + y)",
            "test = 'I\\'m annotated with a function :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a function :)"),
                meta("function", func(args("x", "y"), binary("+", id("x"), id("y")))))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithListValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@list [1, 2, 3]",
            "test = 'I\\'m annotated with a list :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a list :)"),
                meta("list", list(literal(1), literal(2), literal(3))))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithTupleValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@tuple (1, 2, 3)",
            "test = 'I\\'m annotated with a tuple :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a tuple :)"),
                meta("tuple", tuple(literal(1), literal(2), literal(3))))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithMapValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@map { one: 1, two: 2, three: 3 }",
            "test = 'I\\'m annotated with a map :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a map :)"),
                meta("map", map(
                    entry(symbol("one"), literal(1)),
                    entry(symbol("two"), literal(2)),
                    entry(symbol("three"), literal(3))
                )
            ))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithIntegerValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@integer 123",
            "test = 'I\\'m annotated with an integer :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with an integer :)"), meta("integer", literal(123)))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithDoubleValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@double 12.3",
            "test = 'I\\'m annotated with a double :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a double :)"), meta("double", literal(12.3)))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithParentheticalValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@parentheses (x -> say x)",
            "test = 'I\\'m annotated with a parenthetical expression :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a parenthetical expression :)"),
                meta("parentheses", func("x", select(id("say"), args(id("x"))))))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithStringValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@string 'Hello, World!'",
            "test = 'I\\'m annotated with a string :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with a string :)"),
                meta("string", literal("Hello, World!")))
        )));
    }

    @Test
    public void shouldParseMetaAnnotationWithInterpolatedStringValue() throws IddicException {
        ModuleDeclaration module = parse(
            "@interpolation \"Hello, #{world}!\"",
            "test = 'I\\'m annotated with an interpolated string :)'"
        );
        assertThat(module, equalTo(module(
            declaration("test", literal("I'm annotated with an interpolated string :)"),
                meta("interpolation", interpolation(literal("Hello, "), id("world"), literal("!"))))
        )));
    }

    @Test
    public void shouldParseMultipleMetaAnnotations() throws IddicException {
        ModuleDeclaration module = parse(
            "@inventory",
            "@activation { env == :dev }",
            "@scope :application",
            "TestInventory = object {",
            "    numberOfThingies: 10",
            "}"
        );
        assertThat(module, equalTo(module(declaration(
            "TestInventory",
            select(id("object"), args(map(entry(symbol("numberOfThingies"), literal(10))))),
            meta("inventory", nothing()),
            meta("activation", block(binary("==", id("env"), symbol("dev")))),
            meta("scope", symbol("application"))
        ))));
    }

    @Test
    public void shouldParseAccessorAssign() throws IddicException {
        assertThat(parse("test = (apples.color = :red)"), equalTo(module(
            declaration("test", binary("=", select(id("apples"), access("color")), symbol("red")))
        )));
    }

    @Test
    public void shouldParseIndexerAssign() throws IddicException {
        assertThat(parse("test = (apples[:color] = :red)"), equalTo(module(
            declaration("test", binary("=", select(id("apples"), index(symbol("color"))), symbol("red")))
        )));
    }

    @Test
    public void shouldParseTruthyConditional() throws IddicException {
        assertThat(parse("test = if condition then expression end"), equalTo(module(
            declaration("test", conditional(truthy(id("condition"), block(id("expression")))))
        )));
    }

    @Test
    public void shouldParseFalsyConditional() throws IddicException {
        assertThat(parse("test = unless condition then expression end"), equalTo(module(
            declaration("test", conditional(falsy(id("condition"), block(id("expression")))))
        )));
    }

    @Test
    public void shouldParseTruthyConditionWithElse() throws IddicException {
        assertThat(parse("test = if condition then expression else otherExpression end"), equalTo(module(
            declaration("test", conditional(truthy(id("condition"), block(id("expression"))), block(id("otherExpression"))))
        )));
    }

    @Test
    public void shouldParseFalsyConditionalWithElse() throws IddicException {
        assertThat(parse("test = unless condition then expression else otherExpression end"), equalTo(module(
            declaration("test", conditional(falsy(id("condition"), block(id("expression"))), block(id("otherExpression"))))
        )));
    }

    @Test
    public void shouldParseChainedTruthyConditional() throws IddicException {
        ModuleDeclaration module = parse(
            "test = if condition then",
            "    trueResult",
            "else if otherCondition then",
            "    otherResult",
            "else",
            "    falseResult",
            "end"
        );
        assertThat(module, equalTo(module(
            declaration("test", conditional(
                truthy(id("condition"), block(id("trueResult"))),
                truthy(id("otherCondition"), block(id("otherResult"))),
                block(id("falseResult"))
            ))
        )));
    }

    @Test
    public void shouldParseNestedTruthyConditional() throws IddicException {
        ModuleDeclaration module = parse(
            "test = if condition then",
            "    if otherCondition then",
            "        trueResult",
            "    else",
            "        lessTrueResult",
            "    end",
            "else",
            "    falseResult",
            "end"
        );
        assertThat(module, equalTo(module(
            declaration("test", conditional(
                truthy(id("condition"), block(conditional(
                    truthy(id("otherCondition"), block(id("trueResult"))),
                    block(id("lessTrueResult"))))),
                block(id("falseResult"))
            ))
        )));
    }

    @Test
    public void shouldParseMultilineConditional() throws IddicException {
        ModuleDeclaration module = parse(
            "test = if condition then",
            "    do something",
            "    with something here",
            "else if otherCondition then",
            "    do another thing",
            "    with another thing here",
            "else",
            "    do something entirely different",
            "    with other things here",
            "end"
        );
        assertThat(module, equalTo(module(
            declaration("test", conditional(
                truthy(id("condition"), block(
                    select(id("do"), args(id("something"))),
                    select(id("with"), args(id("something"), id("here")))
                )),
                truthy(id("otherCondition"), block(
                    select(id("do"), args(id("another"), id("thing"))),
                    select(id("with"), args(id("another"), id("thing"), id("here")))
                )),
                block(
                    select(id("do"), args(id("something"), id("entirely"), id("different"))),
                    select(id("with"), args(id("other"), id("things"), id("here")))
                )
            ))
        )));
    }

    @Test
    public void shouldParseMemberInOperator() throws IddicException {
        assertThat(parse("test = 4 in [1, 2, 3, 4]"), equalTo(module(
            declaration("test", binary("in", literal(4), list(literal(1), literal(2), literal(3), literal(4))))
        )));
    }

    @Test
    public void shouldParseMemberNotInOperator() throws IddicException {
        assertThat(parse("test = 5 not in [1, 2, 3, 4]"), equalTo(module(
            declaration("test", binary("not in", literal(5), list(literal(1), literal(2), literal(3), literal(4))))
        )));
    }

    @Test
    public void shouldNotInterpolateEscapedHash() throws IddicException {
        assertThat(parse("test = \"not interpolated: \\#{2 + 2}\""), equalTo(module(
            declaration("test", literal("not interpolated: #{2 + 2}"))
        )));
    }

    @Test
    public void shouldParseParentheticalConstantExpression() throws IddicException {
        assertThat(parse("abc = (x y z)"), equalTo(module(
            declaration("abc", select(id("x"), args(id("y"), id("z"))))
        )));
    }

    @Test
    public void shouldParseCharacterLiteral() throws IddicException {
        assertThat(parse("char = c'@'"), equalTo(module(
            declaration("char", literal('@'))
        )));
    }

    @Test
    public void shouldParseNestedInterpolation() throws IddicException {
        assertThat(parse("test = \"#{say \"#{2 + something \"#{4}\"}\"}\""), equalTo(module(
            declaration("test", select(id("say"),
                args(binary("+", literal(2), select(id("something"), args(literal(4)))))
            ))
        )));
    }

    @Test
    public void shouldParseEmbraceExpressionWithEmbrace() throws IddicException {
        assertThat(parse("test = begin something embrace x -> say x ensure oops end"), equalTo(module(
            declaration("test", embrace(
                block(id("something")),
                func(args("x"), block(select(id("say"), args(id("x"))))),
                block(id("oops"))
            ))
        )));
    }

    @Test
    public void shouldParseEmbraceExpressionWithoutEmbrace() throws IddicException {
        assertThat(parse("test = begin something ensure oops end"), equalTo(module(
            declaration("test", embrace(
                block(id("something")),
                VoidExpression.INSTANCE,
                block(id("oops"))
            ))
        )));
    }

    @Test
    public void shouldParseEmbraceExpressionWithoutEnsure() throws IddicException {
        assertThat(parse("test = begin something embrace x -> say x end"), equalTo(module(
            declaration("test", embrace(
                block(id("something")),
                func(args("x"), block(select(id("say"), args(id("x"))))),
                VoidExpression.INSTANCE
            ))
        )));
    }

    @Test
    public void shouldParseEmbraceExpressionMissingBothEmbraceAndEnsure() throws IddicException {
        assertThat(parse("test = begin something end"), equalTo(module(
            declaration("test", embrace(block(id("something")), VoidExpression.INSTANCE, VoidExpression.INSTANCE))
        )));
    }

    @Test
    public void shouldParseMultilineEmbrace() throws IddicException {
        ModuleDeclaration module = parse(
            "test = begin",
            "           do something",
            "           with something here",
            "       embrace x ->",
            "           fix x",
            "           hurl x",
            "       ensure",
            "           database stuff",
            "       end"
        );
        assertThat(module, equalTo(module(
            declaration("test", embrace(
                block(
                    select(id("do"), args(id("something"))),
                    select(id("with"), args(id("something"), id("here")))
                ),
                func(args("x"), block(
                    select(id("fix"), args(id("x"))),
                    hurl(id("x"))
                )),
                block(select(id("database"), args(id("stuff"))))
            ))
        )));
    }

    @Test
    public void shouldParseMultilineEmbraceWithoutEnsure() throws IddicException {
        ModuleDeclaration module = parse(
            "test = begin",
            "           do something",
            "           with something here",
            "       embrace x ->",
            "           fix x",
            "           hurl x",
            "       end"
        );
        assertThat(module, equalTo(module(
            declaration("test", embrace(
                block(
                    select(id("do"), args(id("something"))),
                    select(id("with"), args(id("something"), id("here")))
                ),
                func(args("x"), block(
                    select(id("fix"), args(id("x"))),
                    hurl(id("x"))
                )),
                VoidExpression.INSTANCE
            ))
        )));
    }

    @Test
    public void shouldParseMultilineEmbraceWithoutEmbrace() throws IddicException {
        ModuleDeclaration module = parse(
            "test = begin",
            "           do something",
            "           with something here",
            "       ensure",
            "           database stuff",
            "       end"
        );
        assertThat(module, equalTo(module(
            declaration("test", embrace(
                block(
                    select(id("do"), args(id("something"))),
                    select(id("with"), args(id("something"), id("here")))
                ),
                VoidExpression.INSTANCE,
                block(select(id("database"), args(id("stuff"))))
            ))
        )));
    }

    @Test
    public void shouldNotParse_whenBinaryOperatorOnSecondLine() throws IddicException {
        ModuleDeclaration module = parse(
            "test = one",
            "     + two"
        );
        assertThat(module, equalTo(module(
            declaration("test", id("one"))
        )));
    }

    @Test
    public void shouldParse_whenBinaryOperatorOnFirstLine() throws IddicException {
        ModuleDeclaration module = parse(
            "test = one +",
            "       two"
        );
        assertThat(module, equalTo(module(
            declaration("test", binary("+", id("one"), id("two")))
        )));
    }

    @Test
    public void shouldIgnoreNewLine_whenUsingBackslash() throws IddicException {
        ModuleDeclaration module = parse(
            "test = one \\",
            "     + two"
        );
        assertThat(module, equalTo(module(
            declaration("test", binary("+", id("one"), id("two")))
        )));
    }

    @Test
    public void shouldParseInterpolation() throws IddicException {
        assertThat(parse("form = \"<form action=\\\"#{request.pathInfo}\\\" method=\\\"post\\\"></form>\""), equalTo(module(
            declaration("form", interpolation(
                literal("<form action=\""),
                select(id("request"), access("pathInfo")),
                literal("\" method=\"post\"></form>")
            ))
        )));
    }

    @Test
    public void shouldParseInterpolatedHeredoc() throws IddicException {
        ModuleDeclaration module = parse(
            "form = \"\"\"",
            "<form action=\"#{request.pathInfo}\" method=\"post\">",
            "</form>",
            "\"\"\""
        );
        assertThat(module, equalTo(module(
            declaration("form", interpolation(
                literal("<form action=\""),
                select(id("request"), access("pathInfo")),
                literal("\" method=\"post\">\n</form>\n")
            ))
        )));
    }

    @Test
    public void shouldParseRegex() throws IddicException {
        assertThat(parse("regex = r/this #{thingy}\\/something/i"), equalTo(module(
            declaration("regex", regex(interpolation(
                literal("this "),
                id("thingy"),
                literal("/something")
            ), options("i")))
        )));
    }

    @Test
    public void shouldParseRangeExpression() throws IddicException {
        assertThat(parse("test = 0..9"), equalTo(module(
            declaration("test", binary("..", literal(0), literal(9)))
        )));
    }

    @Test
    public void shouldParseXRangeExpression() throws IddicException {
        assertThat(parse("test = 0...10"), equalTo(module(
            declaration("test", binary("...", literal(0), literal(10)))
        )));
    }

    @Test
    public void shouldParseReturnExpression() throws IddicException {
        assertThat(parse("test = { return True }"), equalTo(module(
            declaration("test", block(result(literal(true))))
        )));
    }

    private Set<String> options(String... options) {
        return new HashSet<>(asList(options));
    }

    private ModuleDeclaration parse(String... inputs) throws IddicException {
        IddicLexer lexer = new IddicLexer(new ANTLRInputStream(join(inputs, '\n')));
        IddicParser parser = new IddicParser(new CommonTokenStream(lexer));
        Translator translator = new Translator();
        return translator.translate(parser.moduleDeclaration());
    }
}
