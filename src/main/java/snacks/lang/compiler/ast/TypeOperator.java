package snacks.lang.compiler.ast;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeOperator extends Type {

    private final String name;
    private final List<Type> parameters;

    public TypeOperator(String name, Type... parameters) {
        this(name, asList(parameters));
    }

    public TypeOperator(String name, Collection<Type> parameters) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public Type genericCopy(SymbolEnvironment environment, Map<Type, Type> mappings) {
        return environment.genericCopy(this, mappings);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TypeOperator) {
            TypeOperator other = (TypeOperator) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(parameters, other.parameters)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        List<Type> types = new ArrayList<>();
        for (Type type : getParameters()) {
            types.add(type.expose());
        }
        return new TypeOperator(name, types);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Type> getConstrainedSet() {
        throw new IllegalStateException("No constraints on TypeOperator!");
    }

    @Override
    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters);
    }

    @Override
    public boolean unifyLeft(Type other) {
        Type left = expose();
        Type right = other.expose();
        return right.unifyRight(left);
    }

    @Override
    public boolean unifyRight(Type other) {
        return unifyParameters(other);
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
    public Type recompose(Type functionType, SymbolEnvironment environment) {
        return this;
    }

    @Override
    public String toString() {
        if (parameters.size() == 2) {
            return "(" + parameters.get(0) + " " + name + " " + parameters.get(1) + ")";
        } else if (parameters.isEmpty()) {
            return name;
        } else {
            return "(" + name + "[" + join(parameters, ", ") + "])";
        }
    }
}