package iddic.lang.compiler.source;

import static iddic.lang.compiler.lexer.Terminal.*;
import static iddic.lang.compiler.source.TokenKindMatcher.hasKind;
import static iddic.lang.compiler.source.TokenValueMatcher.hasValue;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import iddic.lang.compiler.lexer.ScanException;
import iddic.lang.compiler.lexer.TokenSource;
import org.junit.Test;

public class TokenSourceTest {

    @Test
    public void shouldGetId() throws IOException {
        assertThat(scanText("bananas ("), equalTo("bananas"));
    }

    @Test
    public void shouldGetInteger() throws IOException {
        assertThat(scan("123").nextToken(), hasKind(INTEGER));
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
        TokenSource tokens = scan("'this is a string'");
        assertThat(tokens.nextToken(), hasKind(QUOTE));
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(tokens.nextToken(), hasKind(QUOTE));
    }

    @Test
    public void shouldNotEvaluateEscapeSequencesInString() throws IOException {
        TokenSource tokens = scan("'this\\nis\\na\\nstring'");
        tokens.nextToken();
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("this\\nis\\na\\nstring")));
    }

    @Test
    public void shouldNotEvaluateExpressionInString() throws IOException {
        TokenSource tokens = scan("'#{2}'");
        tokens.nextToken();
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("#{2}")));
    }

    @Test
    public void shouldGetInterpolatedString() throws IOException {
        TokenSource tokens = scan("\"this is a string\"");
        assertThat(tokens.nextToken(), hasKind(DQUOTE));
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("this is a string")));
        assertThat(tokens.nextToken(), hasKind(DQUOTE));
    }

    @Test
    public void shouldGetInterpolatedStringWithAsciiEscape() throws IOException {
        TokenSource tokens = scan("\"this\\nis\\na\\nstring\"");
        tokens.nextToken();
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("this\nis\na\nstring")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscape() throws IOException {
        TokenSource tokens = scan("\"Clockwise: \\u27F3\"");
        tokens.nextToken();
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("Clockwise: ⟳")));
    }

    @Test
    public void shouldGetInterpolatedStringWithOctalEscape() throws IOException {
        TokenSource tokens = scan("\"@ octal: \\100\"");
        tokens.nextToken();
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("@ octal: @")));
    }

    @Test
    public void shouldGetInterpolatedStringWithUnicodeEscapeHavingMultipleUs() throws IOException {
        TokenSource tokens = scan("\"Clockwise: \\uu27F3\"");
        tokens.nextToken();
        assertThat(tokens.nextToken(), both(hasKind(STRING)).and(hasValue("Clockwise: ⟳")));
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetInterpolatedStringWithImproperlyCasedUnicodeEscape() throws IOException {
        TokenSource tokens = scan("\"Cyrillic Zhe: \\U0416\"");
        try {
            tokens.nextToken();
        } catch (ScanException exception) {
            fail(exception.getMessage());
        }
        tokens.nextToken();
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetInterpolatedStringWithImproperAsciiEscape() throws IOException {
        TokenSource tokens = scan("\"Oops \\v\"");
        try {
            tokens.nextToken();
        } catch (ScanException exception) {
            fail(exception.getMessage());
        }
        tokens.nextToken();
    }

    private TokenSource scan(String input) throws IOException {
        return new TokenSource(new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8"))));
    }

    private String scanText(String input) throws IOException {
        return (String) scan(input).nextToken().getValue();
    }
}
