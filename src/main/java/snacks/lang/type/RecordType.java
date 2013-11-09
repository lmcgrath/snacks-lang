package snacks.lang.type;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RecordType extends Type {

    private final String name;
    private final List<Type> arguments;
    private final List<Property> properties;

    public RecordType(String name, Collection<Type> arguments, Collection<Property> properties) {
        this.name = name;
        this.arguments = ImmutableList.copyOf(arguments);
        this.properties = ImmutableList.copyOf(properties);
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
        List<Type> exposedArguments = new ArrayList<>();
        for (Type argument : arguments) {
            exposedArguments.add(argument.expose());
        }
        List<Property> exposedProperties = new ArrayList<>();
        for (Property property : properties) {
            exposedProperties.add(property.expose());
        }
        return new RecordType(name, exposedArguments, exposedProperties);
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateRecordType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.copyRecordType(this, mappings);
    }

    @Override
    public List<Type> getArguments() {
        return arguments;
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
    public void print(TypePrinter printer) {
        printer.printRecordType(this);
    }

    @Override
    public String toString() {
        return "(RecordType name=" + name + " arguments=" + arguments + " properties=" + properties + ")";
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

        @Override
        public String toString() {
            return "(Property name=" + name + " type=" + type + ")";
        }
    }
}
