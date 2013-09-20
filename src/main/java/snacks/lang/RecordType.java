package snacks.lang;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RecordType extends Type {

    private final String name;
    private final List<Property> properties;

    public RecordType(String name, Collection<Property> properties) {
        this.name = name;
        this.properties = new ArrayList<>(properties);
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
        List<Property> exposedProperties = new ArrayList<>();
        for (Property property : properties) {
            exposedProperties.add(property.expose());
        }
        return new RecordType(name, exposedProperties);
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateRecordType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.genericCopyOfRecordType(this, mappings);
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "(" + name + properties + ")";
    }

    @Override
    protected boolean unifyRight(Type other) {
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

    public static final class Property {

        private final String name;
        private final Type type;

        public Property(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Property) {
                Property other = (Property) o;
                return new EqualsBuilder()
                    .append(name, other.name)
                    .append(type, other.type)
                    .isEquals();
            } else {
                return false;
            }
        }

        public Property expose() {
            return new Property(name, type.expose());
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }

        public boolean unify(Property property) {
            return Objects.equals(name, property.name) && type.unify(property.type);
        }
    }
}
