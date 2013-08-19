package snacks.lang.compiler.ast;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Type {

    public static final Type INTEGER_TYPE = type("Integer");
    public static final Type STRING_TYPE = type("String");

    public static Type func(Type argument, Type result) {
        return new Type("->", argument, result);
    }

    public static Type type(String name) {
        return new Type(name);
    }

    protected final String name;
    protected final List<Type> parameters;

    public Type(String name, Type... parameters) {
        this.parameters = ImmutableList.copyOf(parameters);
        this.name = name;
    }

    public Type(String name, Collection<Type> parameters) {
        this.parameters = ImmutableList.copyOf(parameters);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Type) {
            Type other = (Type) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(parameters, other.parameters)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters);
    }

    @Override
    public String toString() {
        if (parameters.size() == 2) {
            return "(" + parameters.get(0) + " " + name + " " + parameters.get(1) + ")";
        } else if (parameters.isEmpty()) {
            return name;
        } else {
            return "(" + name + " " + join(parameters) + ")";
        }
    }

    private String join(List<Type> types) {
        StringBuilder builder = new StringBuilder();
        Iterator<Type> iterator = types.iterator();
        builder.append(iterator.next());
        while (iterator.hasNext()) {
            builder.append(", ");
            builder.append(iterator.next());
        }
        return builder.toString();
    }
}
