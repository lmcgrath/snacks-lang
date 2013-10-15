package snacks.lang.parser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RecordMatcher extends VisitableSymbol {

    private final Symbol constructor;
    private final List<Symbol> propertyMatchers;

    public RecordMatcher(Symbol constructor, Collection<Symbol> propertyMatchers) {
        this.constructor = constructor;
        this.propertyMatchers = new ArrayList<>(propertyMatchers);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitRecordMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof RecordMatcher) {
            RecordMatcher other = (RecordMatcher) o;
            return new EqualsBuilder()
                .append(constructor, other.constructor)
                .append(propertyMatchers, other.propertyMatchers)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getConstructor() {
        return constructor;
    }

    public List<Symbol> getPropertyMatchers() {
        return propertyMatchers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, propertyMatchers);
    }

    @Override
    public String toString() {
        return "(RecordMatcher " + constructor + " " + propertyMatchers + ")";
    }
}
