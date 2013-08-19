package snacks.lang.compiler;

import java.util.Set;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import snacks.lang.compiler.ast.AstNode;

public class TranslatorMatcher extends TypeSafeMatcher<Set<AstNode>> {

    @Factory
    public static Matcher<Set<AstNode>> defines(AstNode expectedNode) {
        return new TranslatorMatcher(expectedNode);
    }

    private final AstNode expectedNode;

    private TranslatorMatcher(AstNode expectedNode) {
        this.expectedNode = expectedNode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedNode);
    }

    @Override
    protected boolean matchesSafely(Set<AstNode> item) {
        return item.contains(expectedNode);
    }
}
