package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Declaration extends Symbol {

    private final String name;
    private final Symbol body;
    private final List<Symbol> annotations;

    public Declaration(String name, Symbol body, Symbol... annotations) {
        this.name = name;
        this.body = body;
        this.annotations = asList(annotations);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Declaration) {
            Declaration other = (Declaration) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(body, other.body)
                .append(annotations, other.annotations)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body, annotations);
    }

    @Override
    public String toString() {
        String value = "(" + name + " = " + body + ")";
        if (!annotations.isEmpty()) {
            value += "<" + join(annotations, ", ") + ">";
        }
        return value;
    }
}
