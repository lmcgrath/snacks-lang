package snacks.lang.compiler.scanner;

import java.util.Objects;
import beaver.Symbol;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TokenValueMatcher extends TypeSafeMatcher<Symbol> {

    @Factory
    public static Matcher<Symbol> hasValue(Object value) {
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
    protected void describeMismatchSafely(Symbol token, Description description) {
        description.appendText("was actually ").appendValue(token.value);
    }

    @Override
    protected boolean matchesSafely(Symbol token) {
        return Objects.equals(token.value, value);
    }
}
