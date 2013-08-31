package snacks.lang.ast;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.*;

public class TypeSet extends Type {

    private final Set<Type> types;

    public TypeSet(Collection <Type> types) {
        this.types = new HashSet<>();
        for (Type type : types) {
            if (!this.types.contains(type)) {
                this.types.add(type);
            }
        }
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
        return o == this || o instanceof TypeSet && Objects.equals(types, ((TypeSet) o).types);
    }

    @Override
    public Type expose() {
        List<Type> exposedTypes = new ArrayList<>();
        for (Type type : types) {
            exposedTypes.add(type.expose());
        }
        return new TypeSet(exposedTypes);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.genericCopy(this, mappings);
    }

    public Set<Type> getMembers() {
        return types;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public List<Type> getParameters() {
        return emptyList();
    }

    @Override
    public int hashCode() {
        return Objects.hash(types);
    }

    @Override
    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public String toString() {
        return "(set[" + join(types, ", ") + "])";
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
        return unifyParameters(other);
    }
}
