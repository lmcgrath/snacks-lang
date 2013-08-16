package snacks.lang.compiler.syntax;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RegexLiteral extends Symbol {

    private final List<Symbol> elements;
    private final Set<Character> options;

    public RegexLiteral(List<Symbol> elements, Set<Character> options) {
        this.elements = elements;
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof RegexLiteral) {
            RegexLiteral other = (RegexLiteral) o;
            return new EqualsBuilder()
                .append(elements, other.elements)
                .append(options, other.options)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, options);
    }

    @Override
    public String toString() {
        return "r/" + elements + "/" + options;
    }
}
