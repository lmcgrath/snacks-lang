package iddic.lang.compiler;

import static org.apache.commons.lang.StringUtils.join;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static iddic.lang.compiler.IddicLexer.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

public class IddicLexerTest {

    @Test
    public void shouldGetIdentifier() throws Exception {
        assertThat(scan("hello world!").getType(), equalTo(Id));
    }

    @Test
    public void shouldGetAndKeyword() throws Exception {
        assertThat(scan("and something").getType(), equalTo(LogicalAnd));
    }

    @Test
    public void shouldGetAsKeyword() throws Exception {
        assertThat(scan("as alias").getType(), equalTo(As));
    }

    @Test
    public void shouldGetElseKeyword() throws Exception {
        assertThat(scan("else something").getType(), equalTo(Else));
    }

    @Test
    public void shouldGetFromKeyword() throws Exception {
        assertThat(scan("from iddic.lang import say").getType(), equalTo(From));
    }

    @Test
    public void shouldGetIfKeyword() throws Exception {
        assertThat(scan("if condition").getType(), equalTo(If));
    }

    @Test
    public void shouldGetImportKeyword() throws Exception {
        assertThat(scan("import iddic.lang.say").getType(), equalTo(Import));
    }

    @Test
    public void shouldGetInKeyword() throws Exception {
        assertThat(scan("in [1, 2, 3]").getType(), equalTo(In));
    }

    @Test
    public void shouldGetSquareBraceOpen() throws Exception {
        assertThat(scan("[1, 2, 3]").getType(), equalTo(LSquare));
    }

    @Test
    public void shouldGetIntegerAfterSquareBraceOpen() throws Exception {
        IddicLexer lexer = lexer(" [1]");
        lexer.nextToken();
        assertThat(lexer.nextToken().getType(), equalTo(Integer));
    }

    @Test
    public void shouldGetIsNotKeyword() throws Exception {
        assertThat(scan("is not something").getType(), equalTo(IsNot));
    }

    @Test
    public void shouldGetIsKeyword() throws Exception {
        assertThat(scan("is something").getType(), equalTo(Is));
    }

    @Test
    public void shouldGetNotInKeyword() throws Exception {
        assertThat(scan("not in [1, 2, 3]").getType(), equalTo(NotIn));
    }

    @Test
    public void shouldGetNothingKeyword() throws Exception {
        assertThat(scan("Nothing is Nothing").getType(), equalTo(Nothing));
    }

    @Test
    public void shouldGetNotKeyword() throws Exception {
        assertThat(scan("not something").getType(), equalTo(LogicalNot));
    }

    @Test
    public void shouldGetOrKeyword() throws Exception {
        assertThat(scan("or something").getType(), equalTo(LogicalOr));
    }

    @Test
    public void shouldGetThenKeyword() throws Exception {
        assertThat(scan("then do something").getType(), equalTo(Then));
    }

    @Test
    public void shouldGetUnlessKeyword() throws Exception {
        assertThat(scan("unless condition").getType(), equalTo(Unless));
    }

    @Test
    public void shouldGetVarKeyword() throws Exception {
        assertThat(scan("var test = 'test variable'").getType(), equalTo(Var));
    }

    @Test
    public void shouldGetAccessor() throws Exception {
        assertThat(scan(".something").getType(), equalTo(Dot));
    }

    @Test
    public void shouldGetCurlyBraceOpen() throws Exception {
        assertThat(scan("{ got => something }").getType(), equalTo(LCurly));
    }

    @Test
    public void shouldGetCurlyBraceClose() throws Exception {
        assertThat(scan("} end").getType(), equalTo(RCurly));
    }

    @Test
    public void shouldGetParenthesisOpen() throws Exception {
        assertThat(scan("( a").getType(), equalTo(LParen));
    }

    @Test
    public void shouldGetParenthesisClose() throws Exception {
        assertThat(scan(") something").getType(), equalTo(RParen));
    }

    @Test
    public void shouldGetSquareBraceClose() throws Exception {
        assertThat(scan("].something").getType(), equalTo(RSquare));
    }

    @Test
    public void shouldGetMetaSigil() throws Exception {
        assertThat(scan("@meta").getType(), equalTo(At));
    }

    @Test
    public void shouldGetSeparator() throws Exception {
        assertThat(scan(", ").getType(), equalTo(Comma));
    }

    @Test
    public void shouldGetGoesTo() throws Exception {
        assertThat(scan("=> here").getType(), equalTo(GoesTo));
    }

    @Test
    public void shouldGetEquals() throws Exception {
        assertThat(scan("== something").getType(), equalTo(Equals));
    }

    @Test
    public void shouldGetAssign() throws Exception {
        assertThat(scan("= something").getType(), equalTo(Assign));
    }

    @Test
    public void shouldGetSqlStyleNotEquals() throws Exception {
        assertThat(scan("<> something").getType(), equalTo(NotEquals));
    }

    @Test
    public void shouldGetCStyleNotEquals() throws Exception {
        assertThat(scan("!= something").getType(), equalTo(NotEquals));
    }

    @Test
    public void shouldGetShiftLeft() throws Exception {
        assertThat(scan("<< 3").getType(), equalTo(ShiftLeft));
    }

    @Test
    public void shouldGetLessThan() throws Exception {
        assertThat(scan("< 3").getType(), equalTo(LessThan));
    }

    @Test
    public void shouldGetLessThanEquals() throws Exception {
        assertThat(scan("<= 3").getType(), equalTo(LessThanEquals));
    }

    @Test
    public void shouldGetShiftRight() throws Exception {
        assertThat(scan(">>> 3").getType(), equalTo(UShiftRight));
    }

    @Test
    public void shouldGetSignedShiftRight() throws Exception {
        assertThat(scan(">> 3").getType(), equalTo(ShiftRight));
    }

    @Test
    public void shouldGetGreaterThanEquals() throws Exception {
        assertThat(scan(">= 3").getType(), equalTo(GreaterThanEquals));
    }

    @Test
    public void shouldGetGreaterThan() throws Exception {
        assertThat(scan(">").getType(), equalTo(GreaterThan));
    }

    @Test
    public void shouldGetApply() throws Exception {
        assertThat(scan("$").getType(), equalTo(Apply));
    }

    @Test
    public void shouldGetCStyleLogicalAnd() throws Exception {
        assertThat(scan("&& something").getType(), equalTo(LogicalAnd));
    }

    @Test
    public void shouldGetWordStyleLogicalAnd() throws Exception {
        assertThat(scan("and something").getType(), equalTo(LogicalAnd));
    }

    @Test
    public void shouldGetBitwiseAnd() throws Exception {
        assertThat(scan("& something").getType(), equalTo(BitAnd));
    }

    @Test
    public void shouldGetCStyleLogicalOr() throws Exception {
        assertThat(scan("|| something").getType(), equalTo(LogicalOr));
    }

    @Test
    public void shouldGetWordStyleLogicalOr() throws Exception {
        assertThat(scan("or something").getType(), equalTo(LogicalOr));
    }

    @Test
    public void shouldGetBitwiseOr() throws Exception {
        assertThat(scan("| something").getType(), equalTo(BitOr));
    }

    @Test
    public void shouldGetBitwiseXor() throws Exception {
        assertThat(scan("^ something").getType(), equalTo(BitXor));
    }

    @Test
    public void shouldGetBitwiseNot() throws Exception {
        assertThat(scan("~ something").getType(), equalTo(BitNot));
    }

    @Test
    public void shouldGetPlus() throws Exception {
        assertThat(scan("+ something").getType(), equalTo(Plus));
    }

    @Test
    public void shouldGetAppliesTo() throws Exception {
        assertThat(scan("-> something").getType(), equalTo(AppliesTo));
    }

    @Test
    public void shouldGetMinus() throws Exception {
        assertThat(scan("- something").getType(), equalTo(Minus));
    }

    @Test
    public void shouldGetExponent() throws Exception {
        assertThat(scan("** something").getType(), equalTo(Exponent));
    }

    @Test
    public void shouldGetMultiply() throws Exception {
        assertThat(scan("* something").getType(), equalTo(Multiply));
    }

    @Test
    public void shouldGetDivide() throws Exception {
        assertThat(scan("/ something").getType(), equalTo(Divide));
    }

    @Test
    public void shouldGetModulo() throws Exception {
        assertThat(scan("% something").getType(), equalTo(Modulo));
    }

    @Test
    public void shouldGetCoalesce() throws Exception {
        assertThat(scan("? maybe").getType(), equalTo(Coalesce));
    }

    @Test
    public void shouldGetColon() throws Exception {
        assertThat(scan(": something").getType(), equalTo(Colon));
    }

    @Test
    public void shouldGetSymbol() throws Exception {
        Token token = scan(":symbol something");
        assertThat(token.getType(), equalTo(Symbol));
        assertThat(token.getText(), equalTo(":symbol"));
    }

    @Test
    public void shouldGetKeySymbol() throws Exception {
        Token token = scan("==: something");
        assertThat(token.getType(), equalTo(KeySymbol));
        assertThat(token.getText(), equalTo("==:"));
    }

    @Test
    public void shouldGetIdKeySymbol() throws Exception {
        IddicLexer lexer = lexer("bananas: something");
        assertThat(lexer.nextToken().getType(), equalTo(Id));
        assertThat(lexer.nextToken().getType(), equalTo(KeyColon));
    }

    @Test
    public void shouldGetKeySymbolFollowedByParenthesis() throws Exception {
        IddicLexer lexer = lexer("symbol:(something)");
        assertThat(lexer.nextToken().getType(), equalTo(Id));
        assertThat(lexer.nextToken().getType(), equalTo(KeyColon));
        assertThat(lexer.nextToken().getType(), equalTo(LParen));
    }

    @Test
    public void shouldGetString_whenUsingDoubleQuotes() throws Exception {
        IddicLexer lexer = lexer("\"this is a string\"");
        assertThat(lexer.nextToken().getType(), equalTo(LDQuote));
        assertThat(lexer.nextToken().getText(), equalTo("this is a string"));
        assertThat(lexer.nextToken().getType(), equalTo(RDQuote));
    }

    @Test
    public void shouldGetString_whenUsingSingleQuotes() throws Exception {
        IddicLexer lexer = lexer("'this is a string'");
        assertThat(lexer.nextToken().getType(), equalTo(LQuote));
        assertThat(lexer.nextToken().getText(), equalTo("this is a string"));
        assertThat(lexer.nextToken().getType(), equalTo(RQuote));
    }

    @Test
    public void shouldGetIndexer() throws Exception {
        IddicLexer lexer = lexer("a[test]");
        assertThat(lexer.nextToken().getType(), equalTo(Id));
        Token indexer = lexer.nextToken();
        assertThat(indexer.getType(), equalTo(Indexer));
        assertThat(indexer.getText(), equalTo("["));
    }

    @Test
    public void shouldGetListInIndexer() throws Exception {
        IddicLexer lexer = lexer("a[[1]]");
        lexer.nextToken();
        lexer.nextToken();
        Token lsquare = lexer.nextToken();
        assertThat(lsquare.getType(), equalTo(LSquare));
        assertThat(lsquare.getText(), equalTo("["));
    }

    @Test
    public void shouldGetIndexerSymbolInsideSet() throws Exception {
        IddicLexer lexer = lexer("a {:[], :something}");
        assertThat(lexer.nextToken().getType(), equalTo(Id));
        assertThat(lexer.nextToken().getType(), equalTo(LCurly));
        Token symbol = lexer.nextToken();
        assertThat(symbol.getType(), equalTo(Symbol));
        assertThat(symbol.getText(), equalTo(":[]"));
    }

    @Test
    public void shouldGetColonInsideSet() throws Exception {
        IddicLexer lexer = lexer("a { : }");
        assertThat(lexer.nextToken().getType(), equalTo(Id));
        assertThat(lexer.nextToken().getType(), equalTo(LCurly));
        Token symbol = lexer.nextToken();
        assertThat(symbol.getType(), equalTo(Colon));
        assertThat(symbol.getText(), equalTo(":"));
    }

    @Test
    public void shouldGetKeySymbolInMap() throws Exception {
        IddicLexer lexer = lexer("{ []: 'index'");
        assertThat(lexer.nextToken().getType(), equalTo(LCurly));
        assertThat(lexer.nextToken().getType(), equalTo(KeySymbol));
    }

    @Test
    public void shouldGetNestedInterpolation() throws Exception {
        IddicLexer lexer = lexer("\"#{say \"#{");
        assertThat(lexer.nextToken().getType(), equalTo(LDQuote));
        assertThat(lexer.nextToken().getType(), equalTo(LInterpolate));
        assertThat(lexer.nextToken().getType(), equalTo(Id));
        assertThat(lexer.nextToken().getType(), equalTo(LDQuote));
        assertThat(lexer.nextToken().getType(), equalTo(LInterpolate));
    }

    @Test
    public void shouldGetEscapedInterpolation() throws Exception {
        IddicLexer lexer = lexer("\"escaped: \\#{not interpolated}\"");
        assertThat(lexer.nextToken().getType(), equalTo(LDQuote));
        assertThat(lexer.nextToken().getType(), equalTo(String));
        assertThat(lexer.nextToken().getType(), equalTo(RDQuote));
    }

    @Test
    public void shouldGetHereDoc() throws Exception {
        IddicLexer lexer = lexer("\"\"\"heredoc\"\"\"");
        assertThat(lexer.nextToken().getType(), equalTo(LHereDoc));
        assertThat(lexer.nextToken().getType(), equalTo(String));
        assertThat(lexer.nextToken().getType(), equalTo(RHereDoc));
    }

    @Test
    public void shouldGetNowDoc() throws Exception {
        IddicLexer lexer = lexer("'''nowdoc'''");
        assertThat(lexer.nextToken().getType(), equalTo(LNowDoc));
        assertThat(lexer.nextToken().getType(), equalTo(NowDoc));
        assertThat(lexer.nextToken().getType(), equalTo(RNowDoc));
    }

    @Test
    public void shouldInterpolatedHereDoc() throws Exception {
        IddicLexer lexer = lexer(
            "\"\"\"",
            "<form action=\"#{request.pathInfo}\" method=\"post\">",
            "</form>",
            "\"\"\""
        );
        assertThat(lexer.nextToken().getType(), equalTo(LHereDoc));
        assertThat(lexer.nextToken().getText(), equalTo("<form action=\""));
        assertThat(lexer.nextToken().getType(), equalTo(LInterpolate));
        lexer.nextToken().getType();
        lexer.nextToken().getType();
        lexer.nextToken().getType();
        assertThat(lexer.nextToken().getType(), equalTo(RInterpolate));
        assertThat(lexer.nextToken().getText(), equalTo("\" method=\"post\">\n</form>\n"));
        assertThat(lexer.nextToken().getType(), equalTo(RHereDoc));
    }

    @Test
    public void shouldSkipWhiteSpaceAfterPlus() throws Exception {
        IddicLexer lexer = lexer(
            "2 +",
            "",
            "3"
        );
        assertThat(lexer.nextToken().getType(), equalTo(Integer));
        assertThat(lexer.nextToken().getType(), equalTo(Plus));
        assertThat(lexer.nextToken().getType(), equalTo(Integer));
    }

    private Token scan(String... inputs) throws Exception {
        return lexer(inputs).nextToken();
    }

    private IddicLexer lexer(String... inputs) {
        return new IddicLexer(new ANTLRInputStream(join(inputs, '\n')));
    }
}
