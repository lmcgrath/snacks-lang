package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.record;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;
import snacks.lang.type.RecordType.Property;
import snacks.lang.type.Type;

public class DeclaredRecord extends NamedNode {

    private final String qualifiedName;
    private final List<Type> parameters;
    private final List<Property> properties;

    public DeclaredRecord(String qualifiedName, Collection<Type> parameters, Collection<Property> properties) {
        this.qualifiedName = qualifiedName;
        this.parameters = ImmutableList.copyOf(parameters);
        this.properties = ImmutableList.copyOf(properties);
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
        return record(qualifiedName, parameters, properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, properties);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredRecord(this);
    }

    @Override
    public String toString() {
        return "(Record " + qualifiedName + " " + properties + ")";
    }
}
