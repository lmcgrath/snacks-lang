package snacks.lang.parser;

import static org.apache.commons.lang.StringUtils.join;
import static org.hamcrest.Matchers.both;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static snacks.lang.parser.Terminals.*;
import static snacks.lang.parser.TokenKindMatcher.hasKind;
import static snacks.lang.parser.TokenValueMatcher.hasValue;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import org.junit.Test;

public class ScannerTest {

    @Test
    public void shouldGetInteger() {
        assertThat(scan("123").nextToken(), hasKind(INTEGER));
    }

    @Test
    public void shouldGetDouble() {
        assertThat(scan("12.3").nextToken(), hasKind(DOUBLE));
    }

    @Test
    public void shouldNotGetDoubleWithDotOnly() {
        assertThat(scan("12.oops").nextToken(), hasKind(INTEGER));
    }

    @Test
    public void shouldGetString() {
        Scanner scanner = scan("'this is a string'");
        assertThat(scanner.nextToken(), hasKind(QUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(scanner.nextToken(), hasKind(QUOTE));
    }

    @Test
    public void shouldGetNowdocString() {
        Scanner scanner = scan("'''this is\na string'''");
        assertThat(scanner.nextToken(), hasKind(TRIPLE_QUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is\na string")));
        assertThat(scanner.nextToken(), hasKind(TRIPLE_QUOTE));
    }

    @Test
    public void shouldTrimLeadingNewLineInNowdocString() {
        Scanner scanner = scan("'''\nthis is\na string'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is\na string")));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInString() {
        Scanner scanner = scan("'this\\nis\\na\\nstring'");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInNowdocString() {
        Scanner scanner = scan("'''this\\nis\\na\\nstring'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateExpressionInString() {
        Scanner scanner = scan("'#{2}'");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("#{2}")));
    }

    @Test
    public void shouldNotEvaluateExpressionInNowdocString() {
        Scanner scanner = scan("'''#{2}'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("#{2}")));
    }

    @Test
    public void shouldGetInterpolatedString() {
        Scanner scanner = scan("\"this is a string\"");
        assertThat(scanner.nextToken(), hasKind(DQUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(scanner.nextToken(), hasKind(DQUOTE));
    }

    @Test
    public void shouldGetHeredocString() {
        Scanner scanner = scan("\"\"\"this is a string\"\"\"");
        assertThat(scanner.nextToken(), hasKind(TRIPLE_DQUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(scanner.nextToken(), hasKind(TRIPLE_DQUOTE));
    }

    @Test
    public void shouldGetInterpolatedStringWithAsciiEscape() {
        Scanner scanner = scan("\"this\\nis\\na\\nstring\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldGetHeredocStringWithAsciiEscape() {
        Scanner scanner = scan("\"\"\"this\\nis\\na\\nstring\"\"\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldTrimLeadingNewLineInHeredocString() {
        Scanner scanner = scan("\"\"\"\nthis is a string\"\"\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscape() {
        Scanner scanner = scan("\"Clockwise: \\u27F3\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("Clockwise: ⟳")));
    }

    @Test
    public void shouldGetInterpolatedStringWithOctalEscape() {
        Scanner scanner = scan("\"@ octal: \\100\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("@ octal: @")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscapeHavingMultipleUs() {
        Scanner scanner = scan("\"Clockwise: \\uu27F3\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("Clockwise: ⟳")));
    }

    @Test(expected = ParseException.class)
    public void shouldNotGetInterpolatedStringWithImproperlyCasedUnicodeEscape() {
        Scanner scanner = scan("\"Cyrillic Zhe: \\U0416\"");
        try {
            scanner.nextToken();
        } catch (ParseException exception) {
            fail(exception.getMessage());
        }
        scanner.nextToken();
    }

    @Test(expected = ParseException.class)
    public void shouldNotGetInterpolatedStringWithImproperAsciiEscape() {
        Scanner scanner = scan("\"Oops \\v\"");
        try {
            scanner.nextToken();
        } catch (ParseException exception) {
            fail(exception.getMessage());
        }
        scanner.nextToken();
    }

    @Test
    public void shouldGetLParen() {
        assertThat(scan("(bananas").nextToken(), hasKind(LPAREN));
    }

    @Test
    public void shouldGetRParen() {
        assertThat(scan(")bananas").nextToken(), hasKind(RPAREN));
    }

    @Test
    public void shouldGetIdentifier() {
        assertThat(scan("taco").nextToken(), both(hasKind(IDENTIFIER)).and(hasValue("taco")));
    }

    @Test
    public void shouldGetLIndex() {
        Scanner scanner = scan("bananas[");
        scanner.nextToken();
        assertThat(scanner.nextToken(), hasKind(LINDEX));
    }

    @Test
    public void shouldGetLArg() {
        Scanner scanner = scan("bananas(");
        scanner.nextToken();
        assertThat(scanner.nextToken(), hasKind(LARG));
    }

    @Test
    public void shouldGetComma() {
        assertThat(scan(", bananas").nextToken(), hasKind(COMMA));
    }

    @Test
    public void shouldGetTrue() {
        assertThat(scan("True").nextToken(), hasKind(TRUE));
    }

    @Test
    public void shouldGetFalse() {
        assertThat(scan("False").nextToken(), hasKind(FALSE));
    }

    @Test
    public void shouldGetSymbol() {
        assertThat(scan(":bananas").nextToken(), both(hasKind(SYMBOL)).and(hasValue("bananas")));
    }

    @Test
    public void shouldGetCharacter() {
        assertThat(scan("c'a'").nextToken(), both(hasKind(CHARACTER)).and(hasValue('a')));
    }

    @Test
    public void shouldGetCharacterWithUnicodeEscape() {
        assertThat(scan("c'\\u27F3'").nextToken(), both(hasKind(CHARACTER)).and(hasValue('⟳')));
    }

    @Test
    public void shouldGetCharacterWithOctalEscape() {
        assertThat(scan("c'\\100'").nextToken(), both(hasKind(CHARACTER)).and(hasValue('@')));
    }

    @Test
    public void shouldGetDot() {
        assertThat(scan(".bananas").nextToken(), hasKind(DOT));
    }

    @Test
    public void shouldGetAssign() {
        assertThat(scan("= bananas").nextToken(), hasKind(ASSIGN));
    }

    @Test
    public void shouldGetAppliesTo() {
        assertThat(scan("-> bananas").nextToken(), hasKind(APPLIES_TO));
    }

    @Test
    public void shouldReturnFunctionArgumentsAgainstContainedFunction() {
        Scanner scanner = scan("(a b c -> waffles)");
        assertThat(scanner.nextToken(), hasKind(LFUNC));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("a")));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("b")));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("c")));
        assertThat(scanner.nextToken(), hasKind(APPLIES_TO));
        assertThat(scanner.nextToken(), hasKind(IDENTIFIER));
    }

    @Test
    public void shouldReturnFunctionArgumentsAgainstTailedFunction() {
        Scanner scanner = scan("(a b c) -> waffles");
        assertThat(scanner.nextToken(), hasKind(LFUNC));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("a")));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("b")));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("c")));
        assertThat(scanner.nextToken(), hasKind(RPAREN));
        assertThat(scanner.nextToken(), hasKind(APPLIES_TO));
        assertThat(scanner.nextToken(), hasKind(IDENTIFIER));
    }

    @Test
    public void shouldGetLSquare() {
        assertThat(scan("[bananas").nextToken(), hasKind(LSQUARE));
    }

    @Test
    public void shouldGetGoesTo() {
        assertThat(scan("=> bananas").nextToken(), hasKind(GOES_TO));
    }

    @Test
    public void shouldGetThrowaway() {
        assertThat(scan("_").nextToken(), hasKind(THROWAWAY));
    }

    @Test
    public void shouldGetPositive() {
        assertThat(scan("+bananas").nextToken(), both(hasKind(IDENTIFIER)).and(hasValue("+")));
    }

    @Test
    public void shouldGetDoubleColon() {
        assertThat(scan(":: bananas").nextToken(), hasKind(DOUBLE_COLON));
    }

    @Test
    public void shouldGetQuotedIdentifier() {
        assertThat(scan(" `+` bananas").nextToken(), both(hasKind(QUOTED_IDENTIFIER)).and(hasValue("+")));
    }

    @Test
    public void shouldGetDoubleNegative() {
        Scanner scanner = scan("-(-3)");
        assertThat(scanner.nextToken(), both(hasKind(IDENTIFIER)).and(hasValue("-")));
        assertThat(scanner.nextToken(), hasKind(LPAREN));
        assertThat(scanner.nextToken(), both(hasKind(IDENTIFIER)).and(hasValue("-")));
        assertThat(scanner.nextToken(), both(hasKind(INTEGER)).and(hasValue(3)));
    }

    private Scanner scan(String... inputs) {
        return new Scanner("test", new ByteArrayInputStream(join(inputs, '\n').getBytes(Charset.forName("UTF-8"))));
    }
}
