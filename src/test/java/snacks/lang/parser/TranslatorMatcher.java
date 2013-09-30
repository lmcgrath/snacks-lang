package snacks.lang.parser;

import java.util.Collection;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import snacks.lang.ast.NamedNode;

public class TranslatorMatcher extends TypeSafeMatcher<Collection<NamedNode>> {

    @Factory
    public static Matcher<Collection<NamedNode>> defines(NamedNode expectedNode) {
        return new TranslatorMatcher(expectedNode);
    }

    private final NamedNode expectedNode;

    private TranslatorMatcher(NamedNode expectedNode) {
        this.expectedNode = expectedNode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedNode);
    }

    @Override
    protected boolean matchesSafely(Collection<NamedNode> item) {
        return item.contains(expectedNode);
    }
}
