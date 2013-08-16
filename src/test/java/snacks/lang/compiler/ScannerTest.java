package snacks.lang.compiler;

import static snacks.lang.compiler.Terminals.*;
import static org.hamcrest.Matchers.both;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.apache.commons.lang.StringUtils.join;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;

public class ScannerTest {

    @Test
    public void shouldGetPlus() throws IOException {
        assertThat(scan("+ bananas").nextToken(), TokenKindMatcher.hasKind(PLUS));
    }

    @Test
    public void shouldGetMinus() throws IOException {
        assertThat(scan("- bananas").nextToken(), TokenKindMatcher.hasKind(MINUS));
    }

    @Test
    public void shouldGetMultiply() throws IOException {
        assertThat(scan("* bananas").nextToken(), TokenKindMatcher.hasKind(MULTIPLY));
    }

    @Test
    public void shouldGetDivide() throws IOException {
        assertThat(scan("/ bananas").nextToken(), TokenKindMatcher.hasKind(DIVIDE));
    }

    @Test
    public void shouldGetModulo() throws IOException {
        assertThat(scan("% bananas").nextToken(), TokenKindMatcher.hasKind(MODULO));
    }

    @Test
    public void shouldGetInteger() throws IOException {
        assertThat(scan("123").nextToken(), TokenKindMatcher.hasKind(INTEGER));
    }

    @Test
    public void shouldGetExponent() throws IOException {
        assertThat(scan("** carrots").nextToken(), TokenKindMatcher.hasKind(EXPONENT));
    }

    @Test
    public void shouldGetDouble() throws IOException {
        assertThat(scan("12.3").nextToken(), TokenKindMatcher.hasKind(DOUBLE));
    }

    @Test
    public void shouldNotGetDoubleWithDotOnly() throws IOException {
        assertThat(scan("12.oops").nextToken(), TokenKindMatcher.hasKind(INTEGER));
    }

    @Test
    public void shouldGetString() throws IOException {
        Scanner scanner = scan("'this is a string'");
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(QUOTE));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this is a string")));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(QUOTE));
    }

    @Test
    public void shouldGetNowdocString() throws IOException {
        Scanner scanner = scan("'''this is\na string'''");
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(TRIPLE_QUOTE));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this is\na string")));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(TRIPLE_QUOTE));
    }

    @Test
    public void shouldTrimLeadingNewLineInNowdocString() throws IOException {
        Scanner scanner = scan("'''\nthis is\na string'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this is\na string")));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInString() throws IOException {
        Scanner scanner = scan("'this\\nis\\na\\nstring'");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInNowdocString() throws IOException {
        Scanner scanner = scan("'''this\\nis\\na\\nstring'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateExpressionInString() throws IOException {
        Scanner scanner = scan("'#{2}'");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("#{2}")));
    }

    @Test
    public void shouldNotEvaluateExpressionInNowdocString() throws IOException {
        Scanner scanner = scan("'''#{2}'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("#{2}")));
    }

    @Test
    public void shouldGetInterpolatedString() throws IOException {
        Scanner scanner = scan("\"this is a string\"");
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(DQUOTE));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this is a string")));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(DQUOTE));
    }

    @Test
    public void shouldGetHeredocString() throws IOException {
        Scanner scanner = scan("\"\"\"this is a string\"\"\"");
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(TRIPLE_DQUOTE));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this is a string")));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(TRIPLE_DQUOTE));
    }

    @Test
    public void shouldGetInterpolatedStringWithAsciiEscape() throws IOException {
        Scanner scanner = scan("\"this\\nis\\na\\nstring\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldGetHeredocStringWithAsciiEscape() throws IOException {
        Scanner scanner = scan("\"\"\"this\\nis\\na\\nstring\"\"\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldTrimLeadingNewLineInHeredocString() throws IOException {
        Scanner scanner = scan("\"\"\"\nthis is a string\"\"\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("this is a string")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscape() throws IOException {
        Scanner scanner = scan("\"Clockwise: \\u27F3\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("Clockwise: ⟳")));
    }

    @Test
    public void shouldGetInterpolatedStringWithOctalEscape() throws IOException {
        Scanner scanner = scan("\"@ octal: \\100\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("@ octal: @")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscapeHavingMultipleUs() throws IOException {
        Scanner scanner = scan("\"Clockwise: \\uu27F3\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(STRING)).and(TokenValueMatcher.hasValue("Clockwise: ⟳")));
    }

    @Test(expected = ScannerException.class)
    public void shouldNotGetInterpolatedStringWithImproperlyCasedUnicodeEscape() throws IOException {
        Scanner scanner = scan("\"Cyrillic Zhe: \\U0416\"");
        try {
            scanner.nextToken();
        } catch (ScannerException exception) {
            fail(exception.getMessage());
        }
        scanner.nextToken();
    }

    @Test(expected = ScannerException.class)
    public void shouldNotGetInterpolatedStringWithImproperAsciiEscape() throws IOException {
        Scanner scanner = scan("\"Oops \\v\"");
        try {
            scanner.nextToken();
        } catch (ScannerException exception) {
            fail(exception.getMessage());
        }
        scanner.nextToken();
    }

    @Test
    public void shouldGetLParen() throws IOException {
        assertThat(scan("(bananas").nextToken(), TokenKindMatcher.hasKind(LPAREN));
    }

    @Test
    public void shouldGetRParen() throws IOException {
        assertThat(scan(")bananas").nextToken(), TokenKindMatcher.hasKind(RPAREN));
    }

    @Test
    public void shouldGetIdentifier() throws IOException {
        assertThat(scan("taco").nextToken(), both(TokenKindMatcher.hasKind(IDENTIFIER)).and(TokenValueMatcher.hasValue("taco")));
    }

    @Test
    public void shouldGetBitNot() throws IOException {
        assertThat(scan("~ bananas").nextToken(), TokenKindMatcher.hasKind(BIT_NOT));
    }

    @Test
    public void shouldGetEqual() throws IOException {
        assertThat(scan("== bananas").nextToken(), TokenKindMatcher.hasKind(EQUALS));
    }

    @Test
    public void shouldGetLessThanEquals() throws IOException {
        assertThat(scan("<= bananas").nextToken(), TokenKindMatcher.hasKind(LESS_THAN_EQUALS));
    }

    @Test
    public void shouldGetLShift() throws IOException {
        assertThat(scan("<< bananas").nextToken(), TokenKindMatcher.hasKind(LSHIFT));
    }

    @Test
    public void shouldGetLessThan() throws IOException {
        assertThat(scan("< bananas").nextToken(), TokenKindMatcher.hasKind(LESS_THAN));
    }

    @Test
    public void shouldGetGreaterThanEquals() throws IOException {
        assertThat(scan(">= bananas").nextToken(), TokenKindMatcher.hasKind(GREATER_THAN_EQUALS));
    }

    @Test
    public void shouldGetRShift() throws IOException {
        assertThat(scan(">> bananas").nextToken(), TokenKindMatcher.hasKind(RSHIFT));
    }

    @Test
    public void shouldGetURShift() throws IOException {
        assertThat(scan(">>> bananas").nextToken(), TokenKindMatcher.hasKind(URSHIFT));
    }

    @Test
    public void shouldGetGreaterThan() throws IOException {
        assertThat(scan("> bananas").nextToken(), TokenKindMatcher.hasKind(GREATER_THAN));
    }

    @Test
    public void shouldGetXRange() throws IOException {
        assertThat(scan("... bananas").nextToken(), TokenKindMatcher.hasKind(XRANGE));
    }

    @Test
    public void shouldGetRange() throws IOException {
        assertThat(scan(".. bananas").nextToken(), TokenKindMatcher.hasKind(RANGE));
    }

    @Test
    public void shouldGetBitAnd() throws IOException {
        assertThat(scan("& bananas").nextToken(), TokenKindMatcher.hasKind(BIT_AND));
    }

    @Test
    public void shouldGetBitOr() throws IOException {
        assertThat(scan("| bananas").nextToken(), TokenKindMatcher.hasKind(BIT_OR));
    }

    @Test
    public void shouldGetBitXor() throws IOException {
        assertThat(scan("^ bananas").nextToken(), TokenKindMatcher.hasKind(BIT_XOR));
    }

    @Test
    public void shouldGetCoalesce() throws IOException {
        assertThat(scan("? bananas").nextToken(), TokenKindMatcher.hasKind(COALESCE));
    }

    @Test
    public void shouldGetApply() throws IOException {
        assertThat(scan("$ bananas").nextToken(), TokenKindMatcher.hasKind(APPLY));
    }

    @Test
    public void shouldGetLIndex() throws IOException {
        Scanner scanner = scan("bananas[");
        scanner.nextToken();
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(LINDEX));
    }

    @Test
    public void shouldGetLArg() throws IOException {
        Scanner scanner = scan("bananas(");
        scanner.nextToken();
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(LARG));
    }

    @Test
    public void shouldGetComma() throws IOException {
        assertThat(scan(", bananas").nextToken(), TokenKindMatcher.hasKind(COMMA));
    }

    @Test
    public void shouldGetTrue() throws IOException {
        assertThat(scan("True").nextToken(), TokenKindMatcher.hasKind(TRUE));
    }

    @Test
    public void shouldGetFalse() throws IOException {
        assertThat(scan("False").nextToken(), TokenKindMatcher.hasKind(FALSE));
    }

    @Test
    public void shouldGetNothing() throws IOException {
        assertThat(scan("Nothing").nextToken(), TokenKindMatcher.hasKind(NOTHING));
    }

    @Test
    public void shouldGetNotEquals() throws IOException {
        assertThat(scan("!= bananas").nextToken(), TokenKindMatcher.hasKind(NOT_EQUALS));
        assertThat(scan("<> bananas").nextToken(), TokenKindMatcher.hasKind(NOT_EQUALS));
    }

    @Test
    public void shouldGetAnd() throws IOException {
        assertThat(scan("and bananas").nextToken(), TokenKindMatcher.hasKind(AND));
        assertThat(scan("&& bananas").nextToken(), TokenKindMatcher.hasKind(AND));
    }

    @Test
    public void shouldGetOr() throws IOException {
        assertThat(scan("or bananas").nextToken(), TokenKindMatcher.hasKind(OR));
        assertThat(scan("|| bananas").nextToken(), TokenKindMatcher.hasKind(OR));
    }

    @Test
    public void shouldGetNot() throws IOException {
        assertThat(scan("not bananas").nextToken(), TokenKindMatcher.hasKind(NOT));
    }

    @Test
    public void shouldGetSymbol() throws IOException {
        assertThat(scan(":bananas").nextToken(), both(TokenKindMatcher.hasKind(SYMBOL)).and(TokenValueMatcher.hasValue("bananas")));
    }

    @Test
    public void shouldGetCharacter() throws IOException {
        assertThat(scan("c'a'").nextToken(), both(TokenKindMatcher.hasKind(CHARACTER)).and(TokenValueMatcher.hasValue('a')));
    }

    @Test
    public void shouldGetCharacterWithUnicodeEscape() throws IOException {
        assertThat(scan("c'\\u27F3'").nextToken(), both(TokenKindMatcher.hasKind(CHARACTER)).and(TokenValueMatcher.hasValue('⟳')));
    }

    @Test
    public void shouldGetCharacterWithOctalEscape() throws IOException {
        assertThat(scan("c'\\100'").nextToken(), both(TokenKindMatcher.hasKind(CHARACTER)).and(TokenValueMatcher.hasValue('@')));
    }

    @Test
    public void shouldGetDot() throws IOException {
        assertThat(scan(".bananas").nextToken(), TokenKindMatcher.hasKind(DOT));
    }

    @Test
    public void shouldGetAssign() throws IOException {
        assertThat(scan("= bananas").nextToken(), TokenKindMatcher.hasKind(ASSIGN));
    }

    @Test
    public void shouldGetAppliesTo() throws IOException {
        assertThat(scan("-> bananas").nextToken(), TokenKindMatcher.hasKind(APPLIES_TO));
    }

    @Test
    public void shouldReturnFunctionArgumentsAgainstContainedFunction() throws IOException {
        Scanner scanner = scan("(a b c -> waffles)");
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(LPAREN));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(FWORD)).and(TokenValueMatcher.hasValue("a")));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(FWORD)).and(TokenValueMatcher.hasValue("b")));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(FWORD)).and(TokenValueMatcher.hasValue("c")));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(APPLIES_TO));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(IDENTIFIER));
    }

    @Test
    public void shouldReturnFunctionArgumentsAgainstTailedFunction() throws IOException {
        Scanner scanner = scan("(a b c) -> waffles");
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(LPAREN));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(FWORD)).and(TokenValueMatcher.hasValue("a")));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(FWORD)).and(TokenValueMatcher.hasValue("b")));
        assertThat(scanner.nextToken(), both(TokenKindMatcher.hasKind(FWORD)).and(TokenValueMatcher.hasValue("c")));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(RPAREN));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(APPLIES_TO));
        assertThat(scanner.nextToken(), TokenKindMatcher.hasKind(IDENTIFIER));
    }

    @Test
    public void shouldGetLSquare() throws IOException {
        assertThat(scan("[bananas").nextToken(), TokenKindMatcher.hasKind(LSQUARE));
    }

    @Test
    public void shouldGetKeySymbol() throws IOException {
        assertThat(scan("bananas:").nextToken(), both(TokenKindMatcher.hasKind(KEY_SYMBOL)).and(TokenValueMatcher.hasValue("bananas")));
    }

    @Test
    public void shouldGetGoesTo() throws IOException {
        assertThat(scan("=> bananas").nextToken(), TokenKindMatcher.hasKind(GOES_TO));
    }

    private Scanner scan(String... inputs) throws IOException {
        return new Scanner(new ByteArrayInputStream(join(inputs, '\n').getBytes(Charset.forName("UTF-8"))));
    }
}
