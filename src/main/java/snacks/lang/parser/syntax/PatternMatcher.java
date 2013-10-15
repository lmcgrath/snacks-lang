package snacks.lang.parser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class PatternMatcher extends VisitableSymbol {

    private final List<Symbol> matchers;
    private final Symbol body;

    public PatternMatcher(Collection<Symbol> matchers, Symbol body) {
        this.matchers = new ArrayList<>(matchers);
        this.body = body;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitPatternMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PatternMatcher) {
            PatternMatcher other = (PatternMatcher) o;
            return new EqualsBuilder()
                .append(matchers, other.matchers)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getBody() {
        return body;
    }

    public List<Symbol> getMatchers() {
        return matchers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchers, body);
    }

    @Override
    public String toString() {
        return "(PatternMatcher " + matchers + " -> " + body + ")";
    }
}
