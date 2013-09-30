package snacks.lang.type;

import static com.google.common.collect.ImmutableSortedSet.copyOf;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

public class AlgebraicType extends Type {

    private static final Comparator<Type> comparator = new Comparator<Type>() {
        @Override
        public int compare(Type o1, Type o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private final String name;
    private final Set<Type> types;

    public AlgebraicType(String name, Collection<Type> types) {
        this.name = name;
        this.types = copyOf(comparator, types);
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

    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, types);
    }

    @Override
    public String toString() {
        return "(AlgebraicType " + name + ")";
    }

    @Override
    public boolean unifyLeft(Type other) {
        if (!equals(other)) {
            if (occursIn(other)) {
                return false;
            } else {
                bind(other);
            }
        }
        return true;
    }

    @Override
    public boolean unifyRight(Type other) {
        if (other instanceof AlgebraicType) {
            AlgebraicType otherType = (AlgebraicType) other;
            if (Objects.equals(name, otherType.name) && types.size() == otherType.types.size()) {
                Iterator<Type> theseTypes = types.iterator();
                Iterator<Type> thoseTypes = otherType.types.iterator();
                while (theseTypes.hasNext()) {
                    if (!theseTypes.next().unify(thoseTypes.next())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
