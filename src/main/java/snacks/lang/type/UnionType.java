package snacks.lang.type;

import static org.apache.commons.lang.StringUtils.join;

import java.util.*;

public class UnionType extends Type {

    private final Set<Type> types;

    public UnionType(Collection<Type> types) {
        this.types = new HashSet<>();
        for (Type type : types) {
            if (!this.types.contains(type)) {
                this.types.add(type);
            }
        }
    }

    @Override
    public boolean acceptLeft(Type other, TypeFactory factory) {
        if (!equals(other)) {
            if (occursIn(other, factory)) {
                return false;
            } else {
                bind(other);
            }
        }
        return true;
    }

    @Override
    public boolean acceptRight(Type other, TypeFactory factory) {
        return other instanceof UnionType; // TODO should verify type overlap
    }

    @Override
    public void bind(Type type) {
        if (!types.contains(type) && !equals(type)) {
            types.add(type);
        }
    }

    @Override
    public List<Type> decompose() {
        return new ArrayList<>(types);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof UnionType && Objects.equals(types, ((UnionType) o).types);
    }

    @Override
    public Type expose() {
        List<Type> exposedTypes = new ArrayList<>();
        for (Type type : types) {
            exposedTypes.add(type.expose());
        }
        return new UnionType(exposedTypes);
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateUnionType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.copyUnionType(this, mappings);
    }

    @Override
    public String getName() {
        List<String> names = new ArrayList<>();
        for (Type type : types) {
            names.add(type.getName());
        }
        return "Union<" + join(names, ", ") + ">";
    }

    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public int hashCode() {
        return Objects.hash(types);
    }

    @Override
    public String toString() {
        return "(Union [" + join(types, ", ") + "])";
    }
}
