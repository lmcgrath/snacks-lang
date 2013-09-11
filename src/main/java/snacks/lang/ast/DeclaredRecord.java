package snacks.lang.ast;

import static snacks.lang.Type.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class DeclaredRecord extends AstNode {

    private final String name;
    private final List<DeclaredProperty> properties;

    public DeclaredRecord(String name, Collection<DeclaredProperty> properties) {
        this.name = name;
        this.properties = new ArrayList<>(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredRecord) {
            DeclaredRecord other = (DeclaredRecord) o;
            return new EqualsBuilder()
                .append(name, other.name)
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

    public String getName() {
        return name;
    }

    public List<DeclaredProperty> getProperties() {
        return properties;
    }

    @Override
    public Type getType() {
        List<Type> propTypes = new ArrayList<>();
        for (AstNode property : properties) {
            propTypes.add(property.getType());
        }
        return type(name, propTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }
}
