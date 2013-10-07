package snacks.lang.type;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

public class AlgebraicType extends Type {

    private final String name;
    private final List<Type> types;

    public AlgebraicType(String name, Collection<Type> types) {
        this.name = name;
        this.types = new ArrayList<>(types);
    }

    @Override
    public void bind(Type type) {
        // intentionally empty
    }

    @Override
    public List<Type> decompose() {
        return new ArrayList<>(types);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AlgebraicType) {
            AlgebraicType other = (AlgebraicType) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(types, other.types)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        List<Type> exposedTypes = new ArrayList<>();
        for (Type type : types) {
            exposedTypes.add(type.expose());
        }
        return new AlgebraicType(name, exposedTypes);
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateAlgebraicType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.copyAlgebraicType(this, mappings);
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Type> getTypes() {
        return types;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, types);
    }

    @Override
    public boolean isMember(Type type) {
        for (Type t : types) {
            if (t.accepts(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(AlgebraicType " + name + " " + types + ")";
    }

    @Override
    public boolean acceptRight(Type other) {
        if (other instanceof AlgebraicType) {
            AlgebraicType otherType = (AlgebraicType) other;
            if (Objects.equals(name, otherType.name) && types.size() == otherType.types.size()) {
                Iterator<Type> theseTypes = types.iterator();
                Iterator<Type> thoseTypes = otherType.types.iterator();
                while (theseTypes.hasNext()) {
                    if (!thoseTypes.next().accepts(theseTypes.next())) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            for (Type type : types) {
                if (other.accepts(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
