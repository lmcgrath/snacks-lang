package snacks.lang.parser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RecordDeclaration extends VisitableSymbol {

    private final String name;
    private final List<Symbol> properties;

    public RecordDeclaration(String name, Collection<Symbol> properties) {
        this.name = name;
        this.properties = new ArrayList<>(properties);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitRecordDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof RecordDeclaration) {
            RecordDeclaration other = (RecordDeclaration) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(properties, other.properties)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public List<Symbol> getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }

    @Override
    public String toString() {
        return "(Record " + name + " " + properties + ")";
    }
}
