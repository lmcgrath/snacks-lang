package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Annotation extends Symbol {

    private final List<String> name;
    private final Symbol value;

    public Annotation(String[] name, Symbol value) {
        this.name = asList(name);
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Annotation) {
            Annotation other = (Annotation) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(value, other.value)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "@" + join(name, '.') + (value instanceof NothingLiteral ? "" : "(" + value + ")");
    }
}
