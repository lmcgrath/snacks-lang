package snacks.lang.type;

import static org.apache.commons.lang.StringUtils.join;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ParameterizedType extends Type {

    private final Type type;
    private final List<Type> parameters;

    public ParameterizedType(Type type, Collection<Type> parameters) {
        this.type = type;
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ParameterizedType) {
            ParameterizedType other = (ParameterizedType) o;
            return new EqualsBuilder()
                .append(type, other.type)
                .append(parameters, other.parameters)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        List<Type> exposedParams = new ArrayList<>();
        for (Type param : parameters) {
            exposedParams.add(param.expose());
        }
        return new ParameterizedType(type.expose(), exposedParams);
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateParameterizedType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.copyParameterizedType(this, mappings);
    }

    @Override
    public String getName() {
        List<String> parameterNames = new ArrayList<>();
        for (Type parameter : parameters) {
            parameterNames.add(parameter.getName());
        }
        return type.getName() + "<" + join(parameterNames, ", ") + ">";
    }

    public List<Type> getParameters() {
        return parameters;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, parameters);
    }

    @Override
    public String toString() {
        return "(Parameterized " + type + " " + parameters + ")";
    }

    @Override
    protected boolean acceptRight(Type other) {
        if (other instanceof ParameterizedType) {
            ParameterizedType otherType = (ParameterizedType) other;
            if (parameters.size() == otherType.parameters.size() && otherType.type.accepts(type)) {
                for (int i = 0; i < parameters.size(); i++) {
                    if (!parameters.get(i).accepts(otherType.parameters.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
