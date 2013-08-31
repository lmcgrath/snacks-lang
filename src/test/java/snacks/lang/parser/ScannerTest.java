package snacks.lang.parser;

import static snacks.lang.parser.Terminals.*;
import static org.hamcrest.Matchers.both;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.parser.TokenKindMatcher.hasKind;
import static snacks.lang.parser.TokenValueMatcher.hasValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;

public class ScannerTest {

    @Test
    public void shouldGetPlus() throws IOException {
        assertThat(scan("+ bananas").nextToken(), hasKind(PLUS));
    }

    @Test
    public void shouldGetMinus() throws IOException {
        assertThat(scan("- bananas").nextToken(), hasKind(MINUS));
    }

    @Test
    public void shouldGetMultiply() throws IOException {
        assertThat(scan("* bananas").nextToken(), hasKind(MULTIPLY));
    }

    @Test
    public void shouldGetDivide() throws IOException {
        assertThat(scan("/ bananas").nextToken(), hasKind(DIVIDE));
    }

    @Test
    public void shouldGetModulo() throws IOException {
        assertThat(scan("% bananas").nextToken(), hasKind(MODULO));
    }

    @Test
    public void shouldGetInteger() throws IOException {
        assertThat(scan("123").nextToken(), hasKind(INTEGER));
    }

    @Test
    public void shouldGetExponent() throws IOException {
        assertThat(scan("** carrots").nextToken(), hasKind(EXPONENT));
    }

    @Test
    public void shouldGetDouble() throws IOException {
        assertThat(scan("12.3").nextToken(), hasKind(DOUBLE));
    }

    @Test
    public void shouldNotGetDoubleWithDotOnly() throws IOException {
        assertThat(scan("12.oops").nextToken(), hasKind(INTEGER));
    }

    @Test
    public void shouldGetString() throws IOException {
        Scanner scanner = scan("'this is a string'");
        assertThat(scanner.nextToken(), hasKind(QUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(scanner.nextToken(), hasKind(QUOTE));
    }

    @Test
    public void shouldGetNowdocString() throws IOException {
        Scanner scanner = scan("'''this is\na string'''");
        assertThat(scanner.nextToken(), hasKind(TRIPLE_QUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is\na string")));
        assertThat(scanner.nextToken(), hasKind(TRIPLE_QUOTE));
    }

    @Test
    public void shouldTrimLeadingNewLineInNowdocString() throws IOException {
        Scanner scanner = scan("'''\nthis is\na string'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is\na string")));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInString() throws IOException {
        Scanner scanner = scan("'this\\nis\\na\\nstring'");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInNowdocString() throws IOException {
        Scanner scanner = scan("'''this\\nis\\na\\nstring'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateExpressionInString() throws IOException {
        Scanner scanner = scan("'#{2}'");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("#{2}")));
    }

    @Test
    public void shouldNotEvaluateExpressionInNowdocString() throws IOException {
        Scanner scanner = scan("'''#{2}'''");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("#{2}")));
    }

    @Test
    public void shouldGetInterpolatedString() throws IOException {
        Scanner scanner = scan("\"this is a string\"");
        assertThat(scanner.nextToken(), hasKind(DQUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(scanner.nextToken(), hasKind(DQUOTE));
    }

    @Test
    public void shouldGetHeredocString() throws IOException {
        Scanner scanner = scan("\"\"\"this is a string\"\"\"");
        assertThat(scanner.nextToken(), hasKind(TRIPLE_DQUOTE));
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(scanner.nextToken(), hasKind(TRIPLE_DQUOTE));
    }

    @Test
    public void shouldGetInterpolatedStringWithAsciiEscape() throws IOException {
        Scanner scanner = scan("\"this\\nis\\na\\nstring\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldGetHeredocStringWithAsciiEscape() throws IOException {
        Scanner scanner = scan("\"\"\"this\\nis\\na\\nstring\"\"\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldTrimLeadingNewLineInHeredocString() throws IOException {
        Scanner scanner = scan("\"\"\"\nthis is a string\"\"\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscape() throws IOException {
        Scanner scanner = scan("\"Clockwise: \\u27F3\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("Clockwise: ⟳")));
    }

    @Test
    public void shouldGetInterpolatedStringWithOctalEscape() throws IOException {
        Scanner scanner = scan("\"@ octal: \\100\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("@ octal: @")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscapeHavingMultipleUs() throws IOException {
        Scanner scanner = scan("\"Clockwise: \\uu27F3\"");
        scanner.nextToken();
        assertThat(scanner.nextToken(), both(hasKind(STRING)).and(hasValue("Clockwise: ⟳")));
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
        assertThat(scan("(bananas").nextToken(), hasKind(LPAREN));
    }

    @Test
    public void shouldGetRParen() throws IOException {
        assertThat(scan(")bananas").nextToken(), hasKind(RPAREN));
    }

    @Test
    public void shouldGetIdentifier() throws IOException {
        assertThat(scan("taco").nextToken(), both(hasKind(IDENTIFIER)).and(hasValue("taco")));
    }

    @Test
    public void shouldGetBitNot() throws IOException {
        assertThat(scan("~ bananas").nextToken(), hasKind(BIT_NOT));
    }

    @Test
    public void shouldGetEqual() throws IOException {
        assertThat(scan("== bananas").nextToken(), hasKind(EQUALS));
    }

    @Test
    public void shouldGetLessThanEquals() throws IOException {
        assertThat(scan("<= bananas").nextToken(), hasKind(LESS_THAN_EQUALS));
    }

    @Test
    public void shouldGetLShift() throws IOException {
        assertThat(scan("<< bananas").nextToken(), hasKind(LSHIFT));
    }

    @Test
    public void shouldGetLessThan() throws IOException {
        assertThat(scan("< bananas").nextToken(), hasKind(LESS_THAN));
    }

    @Test
    public void shouldGetGreaterThanEquals() throws IOException {
        assertThat(scan(">= bananas").nextToken(), hasKind(GREATER_THAN_EQUALS));
    }

    @Test
    public void shouldGetRShift() throws IOException {
        assertThat(scan(">> bananas").nextToken(), hasKind(RSHIFT));
    }

    @Test
    public void shouldGetURShift() throws IOException {
        assertThat(scan(">>> bananas").nextToken(), hasKind(URSHIFT));
    }

    @Test
    public void shouldGetGreaterThan() throws IOException {
        assertThat(scan("> bananas").nextToken(), hasKind(GREATER_THAN));
    }

    @Test
    public void shouldGetXRange() throws IOException {
        assertThat(scan("... bananas").nextToken(), hasKind(XRANGE));
    }

    @Test
    public void shouldGetRange() throws IOException {
        assertThat(scan(".. bananas").nextToken(), hasKind(RANGE));
    }

    @Test
    public void shouldGetBitAnd() throws IOException {
        assertThat(scan("& bananas").nextToken(), hasKind(BIT_AND));
    }

    @Test
    public void shouldGetBitOr() throws IOException {
        assertThat(scan("| bananas").nextToken(), hasKind(BIT_OR));
    }

    @Test
    public void shouldGetBitXor() throws IOException {
        assertThat(scan("^ bananas").nextToken(), hasKind(BIT_XOR));
    }

    @Test
    public void shouldGetCoalesce() throws IOException {
        assertThat(scan("? bananas").nextToken(), hasKind(COALESCE));
    }

    @Test
    public void shouldGetApply() throws IOException {
        assertThat(scan("$ bananas").nextToken(), hasKind(APPLY));
    }

    @Test
    public void shouldGetLIndex() throws IOException {
        Scanner scanner = scan("bananas[");
        scanner.nextToken();
        assertThat(scanner.nextToken(), hasKind(LINDEX));
    }

    @Test
    public void shouldGetLArg() throws IOException {
        Scanner scanner = scan("bananas(");
        scanner.nextToken();
        assertThat(scanner.nextToken(), hasKind(LARG));
    }

    @Test
    public void shouldGetComma() throws IOException {
        assertThat(scan(", bananas").nextToken(), hasKind(COMMA));
    }

    @Test
    public void shouldGetTrue() throws IOException {
        assertThat(scan("True").nextToken(), hasKind(TRUE));
    }

    @Test
    public void shouldGetFalse() throws IOException {
        assertThat(scan("False").nextToken(), hasKind(FALSE));
    }

    @Test
    public void shouldGetNothing() throws IOException {
        assertThat(scan("Nothing").nextToken(), hasKind(NOTHING));
    }

    @Test
    public void shouldGetNotEquals() throws IOException {
        assertThat(scan("!= bananas").nextToken(), hasKind(NOT_EQUALS));
        assertThat(scan("<> bananas").nextToken(), hasKind(NOT_EQUALS));
    }

    @Test
    public void shouldGetAnd() throws IOException {
        assertThat(scan("and bananas").nextToken(), hasKind(AND));
        assertThat(scan("&& bananas").nextToken(), hasKind(AND));
    }

    @Test
    public void shouldGetOr() throws IOException {
        assertThat(scan("or bananas").nextToken(), hasKind(OR));
        assertThat(scan("|| bananas").nextToken(), hasKind(OR));
    }

    @Test
    public void shouldGetNot() throws IOException {
        assertThat(scan("not bananas").nextToken(), hasKind(NOT));
    }

    @Test
    public void shouldGetSymbol() throws IOException {
        assertThat(scan(":bananas").nextToken(), both(hasKind(SYMBOL)).and(hasValue("bananas")));
    }

    @Test
    public void shouldGetCharacter() throws IOException {
        assertThat(scan("c'a'").nextToken(), both(hasKind(CHARACTER)).and(hasValue('a')));
    }

    @Test
    public void shouldGetCharacterWithUnicodeEscape() throws IOException {
        assertThat(scan("c'\\u27F3'").nextToken(), both(hasKind(CHARACTER)).and(hasValue('⟳')));
    }

    @Test
    public void shouldGetCharacterWithOctalEscape() throws IOException {
        assertThat(scan("c'\\100'").nextToken(), both(hasKind(CHARACTER)).and(hasValue('@')));
    }

    @Test
    public void shouldGetDot() throws IOException {
        assertThat(scan(".bananas").nextToken(), hasKind(DOT));
    }

    @Test
    public void shouldGetAssign() throws IOException {
        assertThat(scan("= bananas").nextToken(), hasKind(ASSIGN));
    }

    @Test
    public void shouldGetAppliesTo() throws IOException {
        assertThat(scan("-> bananas").nextToken(), hasKind(APPLIES_TO));
    }

    @Test
    public void shouldReturnFunctionArgumentsAgainstContainedFunction() throws IOException {
        Scanner scanner = scan("(a b c -> waffles)");
        assertThat(scanner.nextToken(), hasKind(LFUNC));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("a")));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("b")));
        assertThat(scanner.nextToken(), both(hasKind(FWORD)).and(hasValue("c")));
        assertThat(scanner.nextToken(), hasKind(APPLIES_TO));
        assertThat(scanner.nextToken(), hasKind(IDENTIFIER));
    }

    @Test
    public void shouldReturnFunctionArgumentsAgainstTailedFunction() throws IOException {
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
    public void shouldGetLSquare() throws IOException {
        assertThat(scan("[bananas").nextToken(), hasKind(LSQUARE));
    }

    @Test
    public void shouldGetKeySymbol() throws IOException {
        assertThat(scan("bananas:").nextToken(), both(hasKind(KEY_SYMBOL)).and(hasValue("bananas")));
    }

    @Test
    public void shouldGetGoesTo() throws IOException {
        assertThat(scan("=> bananas").nextToken(), hasKind(GOES_TO));
    }

    @Test
    public void shouldGetThrowaway() throws IOException {
        assertThat(scan("_").nextToken(), hasKind(THROWAWAY));
    }

    private Scanner scan(String... inputs) throws IOException {
        return new Scanner(new ByteArrayInputStream(join(inputs, '\n').getBytes(Charset.forName("UTF-8"))));
    }
}
