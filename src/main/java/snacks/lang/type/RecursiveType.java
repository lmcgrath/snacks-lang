package snacks.lang.type;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;

public class RecursiveType extends Type {

    private final String name;
    private final List<Type> arguments;

    public RecursiveType(String name, Collection<Type> arguments) {
        this.name = name;
        this.arguments = ImmutableList.copyOf(arguments);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof RecursiveType) {
            RecursiveType other = (RecursiveType) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(arguments, other.arguments)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        return this;
    }

    @Override
    public List<Type> getArguments() {
        return arguments;
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateRecursiveType(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.copyRecursiveType(this, mappings);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void print(TypePrinter printer) {
        printer.printRecursiveType(this);
    }

    @Override
    public String toString() {
        return "(Recur name=" + name + " arguments=" + arguments + ")";
    }
}
