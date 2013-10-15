package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class NamedPattern extends VisitableSymbol {

    private final String name;
    private final Symbol pattern;

    public NamedPattern(String name, Symbol pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitNamedPattern(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof NamedPattern) {
            NamedPattern other = (NamedPattern) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(pattern, other.pattern)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getPattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pattern);
    }

    @Override
    public String toString() {
        return "(def " + name + " = " + pattern + ")";
    }
}
