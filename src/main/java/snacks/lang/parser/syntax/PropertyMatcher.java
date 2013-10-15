package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class PropertyMatcher extends VisitableSymbol {

    private final String name;
    private final Symbol matcher;

    public PropertyMatcher(String name, Symbol matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitPropertyMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PropertyMatcher) {
            PropertyMatcher other = (PropertyMatcher) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(matcher, other.matcher)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getMatcher() {
        return matcher;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, matcher);
    }

    @Override
    public String toString() {
        return "(PropertyMatcher " + name + " = " + matcher + ")";
    }
}
