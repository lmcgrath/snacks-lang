package snacks.lang.type;

import static java.util.Arrays.asList;

import java.util.*;
import com.google.common.collect.ImmutableList;

public abstract class Type {

    protected static List<Type> expose(Collection<Type> types) {
        List<Type> exposedTypes = new ArrayList<>();
        for (Type type : types) {
            exposedTypes.add(type.expose());
        }
        return exposedTypes;
    }

    public void bind(Type type) {
        // intentionally empty
    }

    public List<Type> decompose() {
        return asList(this);
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract Type expose();

    public abstract void generate(TypeGenerator generator);

    public abstract Type genericCopy(TypeFactory types, Map<Type, Type> mappings);

    public List<Type> getArguments() {
        return ImmutableList.of();
    }

    public abstract String getName();

    @Override
    public abstract int hashCode();

    public abstract void print(TypePrinter printer);

    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public abstract String toString();
}
