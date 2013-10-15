package snacks.lang.parser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ConstructorMatcher extends VisitableSymbol {

    private final Symbol constructor;
    private final List<Symbol> argumentMatchers;

    public ConstructorMatcher(Symbol constructor, Collection<Symbol> argumentMatchers) {
        this.constructor = constructor;
        this.argumentMatchers = new ArrayList<>(argumentMatchers);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitConstructorMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ConstructorMatcher) {
            ConstructorMatcher other = (ConstructorMatcher) o;
            return new EqualsBuilder()
                .append(constructor, other.constructor)
                .append(argumentMatchers, other.argumentMatchers)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getArgumentMatchers() {
        return argumentMatchers;
    }

    public Symbol getConstructor() {
        return constructor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, argumentMatchers);
    }

    @Override
    public String toString() {
        return "(ConstructorMatcher " + constructor + " " + argumentMatchers + ")";
    }
}
