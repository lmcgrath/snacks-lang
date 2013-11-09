package snacks.lang.type;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;

public class AlgebraicType extends Type {

    private final String name;
    private final List<Type> arguments;
    private final List<Type> options;

    public AlgebraicType(String name, Collection<Type> arguments, Collection<Type> options) {
        this.name = name;
        this.arguments = ImmutableList.copyOf(arguments);
        this.options = ImmutableList.copyOf(options);
    }

    @Override
    public void bind(Type type) {
        // intentionally empty
    }

    @Override
    public List<Type> decompose() {
        return new ArrayList<>(options);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof AlgebraicType) {
            AlgebraicType other = (AlgebraicType) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(arguments, other.arguments)
                .append(options, other.options)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        return new AlgebraicType(name, expose(arguments), expose(options));
    }

    @Override
    public List<Type> getArguments() {
        return arguments;
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

    public List<Type> getOptions() {
        return options;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, options);
    }

    @Override
    public void print(TypePrinter printer) {
        printer.printAlgebraicType(this);
    }

    @Override
    public String toString() {
        return "(AlgebraicType name=" + name + " arguments=" + arguments + " options=" + options + ")";
    }
}
