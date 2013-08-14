package iddic.lang.compiler.source;

import iddic.lang.compiler.lexer.Terminal;
import iddic.lang.compiler.lexer.Token;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TokenKindMatcher extends TypeSafeMatcher<Token> {

    @Factory
    public static Matcher<Token> hasKind(Terminal kind) {
        return new TokenKindMatcher(kind);
    }

    private final Terminal kind;

    public TokenKindMatcher(Terminal kind) {
        this.kind = kind;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has kind ").appendValue(kind);
    }

    @Override
    protected void describeMismatchSafely(Token token, Description description) {
        description.appendText("had kind ").appendValue(token.getKind());
    }

    @Override
    protected boolean matchesSafely(Token token) {
        return token.getKind() == kind;
    }
}
