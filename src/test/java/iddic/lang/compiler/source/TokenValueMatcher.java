package iddic.lang.compiler.source;

import java.util.Objects;
import iddic.lang.compiler.lexer.Token;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TokenValueMatcher extends TypeSafeMatcher<Token> {

    @Factory
    public static Matcher<Token> hasValue(Object value) {
        return new TokenValueMatcher(value);
    }

    private final Object value;

    public TokenValueMatcher(Object value) {
        this.value = value;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("value ").appendValue(value);
    }

    @Override
    protected void describeMismatchSafely(Token token, Description description) {
        description.appendText("value ").appendValue(token.getValue());
    }

    @Override
    protected boolean matchesSafely(Token token) {
        return Objects.equals(token.getValue(), value);
    }
}
