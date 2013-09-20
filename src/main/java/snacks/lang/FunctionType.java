package snacks.lang;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class FunctionType extends Type {

    private final Type argument;
    private final Type result;

    public FunctionType(Type argument, Type result) {
        this.argument = argument;
        this.result = result;
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
        } else if (o instanceof FunctionType) {
            FunctionType other = (FunctionType) o;
            return new EqualsBuilder()
                .append(argument, other.argument)
                .append(result, other.result)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        return new FunctionType(argument.expose(), result.expose());
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateFunctionType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.genericCopy(this, mappings);
    }

    public Type getArgument() {
        return argument;
    }

    @Override
    public String getName() {
        return "->";
    }

    @Override
    protected boolean contains(Type type) {
        return type.occursIn(argument) || type.occursIn(result);
    }

    public Type getResult() {
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, result);
    }

    @Override
    public Type recompose(Type functionType, TypeFactory types) {
        return this;
    }

    @Override
    public String toString() {
        return "(" + argument + " -> " + result + ")";
    }

    @Override
    public boolean unifyLeft(Type other) {
        Type left = expose();
        Type right = other.expose();
        return right.unifyRight(left);
    }

    @Override
    public boolean unifyRight(Type other) {
        if (other instanceof FunctionType) {
            FunctionType otherFunction = (FunctionType) other;
            return argument.unify(otherFunction.argument)
                && result.unify(otherFunction.result);
        } else {
            return false;
        }
    }
}
