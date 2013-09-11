package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class PropertyDeclaration extends Symbol implements Visitable {

    private final String name;
    private final Symbol type;

    public PropertyDeclaration(String name, Symbol type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitPropertyDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PropertyDeclaration) {
            PropertyDeclaration other = (PropertyDeclaration) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "(Property " + name + " :: " + type + ")";
    }
}
