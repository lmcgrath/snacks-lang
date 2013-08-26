package snacks.lang.compiler.syntax;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class RegexLiteral extends Symbol implements Visitable {

    private final List<Symbol> elements;
    private final Set<Character> options;

    public RegexLiteral(List<Symbol> elements, Set<Character> options) {
        this.elements = elements;
        this.options = options;
    }

    @Override
    public void accept(SyntaxVisitor visitor) throws SnacksException {
        visitor.visitRegexLiteral(this);
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

    public List<Symbol> getElements() {
        return elements;
    }

    public Set<Character> getOptions() {
        return options;
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
