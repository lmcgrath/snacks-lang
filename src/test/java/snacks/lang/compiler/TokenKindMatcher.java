package snacks.lang.compiler;

import beaver.Symbol;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TokenKindMatcher extends TypeSafeMatcher<Symbol> {

    @Factory
    public static Matcher<Symbol> hasKind(short kind) {
        return new TokenKindMatcher(kind);
    }

    private final short kind;

    public TokenKindMatcher(short kind) {
        this.kind = kind;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("kind ").appendValue(kind);
    }

    @Override
    protected void describeMismatchSafely(Symbol token, Description description) {
        description.appendText("was actually ").appendValue(token.getId());
    }

    @Override
    protected boolean matchesSafely(Symbol token) {
        return token.getId() == kind;
    }
}
