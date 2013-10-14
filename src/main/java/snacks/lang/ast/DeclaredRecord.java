package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;
import snacks.lang.type.RecordType.Property;
import snacks.lang.type.Type;

public class DeclaredRecord extends NamedNode {

    private final String qualifiedName;
    private final List<Property> properties;

    public DeclaredRecord(String qualifiedName, Collection<Property> properties) {
        this.qualifiedName = qualifiedName;
        this.properties = new ArrayList<>(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredRecord) {
            DeclaredRecord other = (DeclaredRecord) o;
            return new EqualsBuilder()
                .append(qualifiedName, other.qualifiedName)
                .append(properties, other.properties)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredRecord(this);
    }

    @Override
    public SnackKind getKind() {
        return TYPE;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public Type getType() {
        return record(qualifiedName, properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, properties);
    }

    @Override
    public String toString() {
        return "(Record " + qualifiedName + " " + properties + ")";
    }
}
