package iddic.lang.compiler;

import static iddic.lang.compiler.Terminals.DOUBLE;
import static iddic.lang.compiler.Terminals.DOUBLE_QUOTE;
import static iddic.lang.compiler.Terminals.INT;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TokenSourceTest {

    @Test
    public void shouldGetId() {
        assertThat(scanText("bananas ("), equalTo("bananas"));
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetId() {
        scan("bananas\"oops").nextToken();
    }

    @Test
    public void shouldGetInteger() {
        assertThat(scan("123").nextToken().getKind(), equalTo(INT));
    }

    @Test
    public void shouldGetDouble() {
        assertThat(scan("12.3").nextToken().getKind(), equalTo(DOUBLE));
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetInteger() {
        scan("123oops").nextToken();
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetDoubleWithDotOnly() {
        scan("12.oops").nextToken();
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetDouble() {
        scan("12.3oops").nextToken();
    }

    @Test
    public void shouldGetString() {
        TokenSource tokens = scan("\"this is a string\"");
        assertThat(tokens.nextToken().getKind(), equalTo(DOUBLE_QUOTE));
        assertThat(tokens.nextToken().getText(), equalTo("this is a string"));
        assertThat(tokens.nextToken().getKind(), equalTo(DOUBLE_QUOTE));
    }

    @Test
    public void shouldGetStringWithAsciiEscape() {
        TokenSource tokens = scan("\"this\\nis\\na\\nstring\"");
        tokens.nextToken();
        Token string = tokens.nextToken();
        assertThat(string.getValue(), equalTo((Object) "this\nis\na\nstring"));
        assertThat(string.getText(), equalTo((Object) "this\\nis\\na\\nstring"));
        assertThat(tokens.nextToken().getKind(), equalTo(DOUBLE_QUOTE));
    }

    @Test
    public void shouldGetStringWithUnicodeEscape() {
        TokenSource tokens = scan("\"Clockwise: \\u27F3\"");
        tokens.nextToken();
        Token string = tokens.nextToken();
        assertThat(string.getValue(), equalTo((Object) "Clockwise: ⟳"));
        assertThat(string.getText(), equalTo((Object) "Clockwise: \\u27F3"));
        assertThat(tokens.nextToken().getKind(), equalTo(DOUBLE_QUOTE));
    }

    @Test
    public void shouldGetStringWithUnicodeEscapeHavingMultipleUs() {
        TokenSource tokens = scan("\"Cyrillic Zhe: \\uu0416\"");
        tokens.nextToken();
        Token string = tokens.nextToken();
        assertThat(string.getValue(), equalTo((Object) "Cyrillic Zhe: Ж"));
        assertThat(string.getText(), equalTo((Object) "Cyrillic Zhe: \\uu0416"));
        assertThat(tokens.nextToken().getKind(), equalTo(DOUBLE_QUOTE));
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetStringWithImproperlyCasedUnicodeEscape() {
        TokenSource tokens = scan("\"Cyrillic Zhe: \\U0416\"");
        try {
            tokens.nextToken();
        } catch (ScanException exception) {
            fail(exception.getMessage());
        }
        tokens.nextToken();
    }

    @Test(expected = ScanException.class)
    public void shouldNotGetStringWithImproperAsciiEscape() {
        TokenSource tokens = scan("\"Oops \\v\"");
        try {
            tokens.nextToken();
        } catch (ScanException exception) {
            fail(exception.getMessage());
        }
        tokens.nextToken();
    }

    private TokenSource scan(String input) {
        return new TokenSource(new StringStream(input));
    }

    private String scanText(String input) {
        return scan(input).nextToken().getText();
    }
}
