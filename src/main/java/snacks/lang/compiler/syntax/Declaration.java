package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Declaration extends Symbol {

    private final String name;
    private final Symbol body;

    public Declaration(String name, Symbol body) {
        this.name = name;
        this.body = body;
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
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body);
    }

    @Override
    public String toString() {
        return "(def " + name + " = " + body + ")";
    }
}
