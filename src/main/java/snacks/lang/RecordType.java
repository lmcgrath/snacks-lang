package snacks.lang;

import static java.util.Arrays.asList;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RecordType extends Type {

    private final String name;
    private final List<PropertyType> properties;

    public RecordType(String name, Collection<PropertyType> properties) {
        this.name = name;
        this.properties = new ArrayList<>(properties);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.genericCopy(this, mappings);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof RecordType) {
            RecordType other = (RecordType) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(properties, other.properties)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        List<PropertyType> exposedProperties = new ArrayList<>();
        for (PropertyType property : properties) {
            exposedProperties.add(property.expose());
        }
        return new RecordType(name, exposedProperties);
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateRecordType(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected boolean contains(Type type) {
        return type.occursIn(new ArrayList<Type>(properties));
    }

    public List<PropertyType> getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public boolean unifyLeft(Type other) {
        Type left = expose();
        Type right = other.expose();
        return right.unifyRight(left);
    }

    @Override
    public boolean unifyRight(Type other) {
        if (other instanceof RecordType) {
            RecordType otherRecord = (RecordType) other;
            if (name.equals(otherRecord.name)) {
                for (int i = 0; i < properties.size(); i++) {
                    if (!properties.get(i).unify(otherRecord.properties.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void bind(Type type) {
        // intentionally empty
    }

    @Override
    public List<Type> decompose() {
        return asList((Type) this);
    }

    @Override
    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public String toString() {
        return "(" + name + properties + ")";
    }
}
