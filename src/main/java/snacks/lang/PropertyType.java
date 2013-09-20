package snacks.lang;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class PropertyType extends Type {

    private final String name;
    private final Type type;

    public PropertyType(String name, Type type) {
        this.name = name;
        this.type = type;
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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PropertyType) {
            PropertyType other = (PropertyType) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public PropertyType expose() {
        return new PropertyType(name, type.expose());
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generatePropertyType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.genericCopy(this, mappings);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected boolean contains(Type type) {
        return false;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public String toString() {
        return "(" + name + ": " + type + ")";
    }

    @Override
    public boolean unifyLeft(Type other) {
        Type left = expose();
        Type right = other.expose();
        return right.unifyRight(left);
    }

    @Override
    public boolean unifyRight(Type other) {
        if (other instanceof PropertyType) {
            PropertyType otherProperty = (PropertyType) other;
            if (name.equals(otherProperty.name)) {
                return type.unify(otherProperty.type);
            }
        }
        return false;
    }
}
