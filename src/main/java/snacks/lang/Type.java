package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.JavaUtils.javaName;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.SnacksList.listOf;
import static snacks.lang.SnacksList.toList;
import static snacks.lang.Type.AlgebraicType.algebraicType;
import static snacks.lang.Type.FunctionType.functionType;
import static snacks.lang.Type.RecordType.Property.propertyType;
import static snacks.lang.Type.RecordType.recordType;
import static snacks.lang.Type.RecursiveType.recursiveType;
import static snacks.lang.Type.SimpleType.simpleType;
import static snacks.lang.Type.UnionType.unionType;
import static snacks.lang.Type.VariableType.variableType;
import static snacks.lang.Types.*;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksList.EmptyList;

@Snack(name = "Type", kind = TYPE)
public abstract class Type {

    @SnackType
    public static Type type() {
        return algebraic("snacks.lang.Type", asList(
            algebraicType(),
            functionType(),
            recordType(),
            recursiveType(),
            simpleType(),
            unionType(),
            variableType()
        ));
    }

    protected static SnacksList<Type> expose(Iterable<Type> types) {
        List<Type> exposedTypes = new ArrayList<>();
        for (Type type : types) {
            exposedTypes.add(type.expose());
        }
        return toList(exposedTypes);
    }

    private Type() {
        // intentionally empty
    }

    public void bind(Type type) {
        // intentionally empty
    }

    public SnacksList<Type> decompose() {
        return toList(this);
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract Type expose();

    public abstract void generate(TypeGenerator generator);

    public abstract Type genericCopy(TypeFactory types, Map<Type, Type> mappings);

    public SnacksList<Type> getArguments() {
        return EmptyList.value();
    }

    public String getJavaName() {
        String name = getName().getValue();
        return javaName(name.substring(0, name.lastIndexOf('.'))) + '.' + javaName(name.substring(name.lastIndexOf('.') + 1));
    }

    public abstract Symbol getName();

    @Override
    public abstract int hashCode();

    public abstract void print(TypePrinter printer);

    public Type recompose(Type type, TypeFactory types) {
        return this;
    }

    @Override
    public abstract String toString();

    @Snack(name = "AlgebraicType", kind = TYPE)
    public static class AlgebraicType extends Type {

        @SnackType
        public static Type algebraicType() {
            return record("snacks.lang.AlgebraicType", asList(
                property("name", symbolType()),
                property("arguments", listOf(recur("snacks.lang.Type"))),
                property("options", listOf(recur("snacks.lang.Type")))
            ));
        }

        private final Symbol name;
        private final SnacksList<Type> arguments;
        private final SnacksList<Type> options;

        public AlgebraicType(String name, Iterable<Type> arguments, Iterable<Type> options) {
            this(Symbol.valueOf(name), arguments, options);
        }

        public AlgebraicType(Symbol name, Iterable<Type> arguments, Iterable<Type> options) {
            this.name = name;
            this.arguments = toList(arguments);
            this.options = toList(options);
        }

        @Override
        public void bind(Type type) {
            // intentionally empty
        }

        @Override
        public SnacksList<Type> decompose() {
            return options;
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
        public void generate(TypeGenerator generator) {
            generator.generateAlgebraicType(this);
        }

        @Override
        public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
            return types.copyAlgebraicType(this, mappings);
        }

        @Override
        public SnacksList<Type> getArguments() {
            return arguments;
        }

        @Override
        public Symbol getName() {
            return name;
        }

        public SnacksList<Type> getOptions() {
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
            return "AlgebraicType{name=" + name + ", arguments=" + arguments + ", options=" + options + "}";
        }
    }

    @Snack(name = "FunctionType", kind = TYPE)
    public static class FunctionType extends Type {

        private static final Symbol name = Symbol.valueOf("snacks.lang.FunctionType");

        @SnackType
        public static Type functionType() {
            return record(name, SnacksList.toList(
                property("name", symbolType()),
                property("argument", recur("snacks.lang.Type")),
                property("result", recur("snacks.lang.Type"))
            ));
        }

        private final Type argument;
        private final Type result;

        public FunctionType(Type argument, Type result) {
            this.argument = argument;
            this.result = result;
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
            return types.copyFunctionType(this, mappings);
        }

        public Type getArgument() {
            return argument;
        }

        @Override
        public Symbol getName() {
            return name;
        }

        public Type getResult() {
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(argument, result);
        }

        @Override
        public void print(TypePrinter printer) {
            printer.printFunctionType(this);
        }

        @Override
        public String toString() {
            return "FunctionType{argument=" + argument + ", result=" + result + "}";
        }
    }

    @Snack(name = "RecordType", kind = TYPE)
    public static class RecordType extends Type {

        @SnackType
        public static Type recordType() {
            return record("snacks.lang.RecordType", asList(
                property("name", symbolType()),
                property("arguments", listOf(recur("snacks.lang.Type"))),
                property("properties", listOf(propertyType()))
            ));
        }

        private final Symbol name;
        private final SnacksList<Type> arguments;
        private final SnacksList<Property> properties;

        public RecordType(String name, Iterable<Type> arguments, Iterable<Property> properties) {
            this(Symbol.valueOf(name), arguments, properties);
        }

        public RecordType(Symbol name, Iterable<Type> arguments, Iterable<Property> properties) {
            this.name = name;
            this.arguments = toList(arguments);
            this.properties = toList(properties);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof RecordType) {
                RecordType other = (RecordType) o;
                return new EqualsBuilder()
                    .append(name, other.name)
                    .append(properties, other.properties)
                    .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public Type expose() {
            List<Type> exposedArguments = new ArrayList<>();
            for (Type argument : arguments) {
                exposedArguments.add(argument.expose());
            }
            List<Property> exposedProperties = new ArrayList<>();
            for (Property property : properties) {
                exposedProperties.add(property.expose());
            }
            return new RecordType(name, exposedArguments, exposedProperties);
        }

        @Override
        public void generate(TypeGenerator generator) {
            generator.generateRecordType(this);
        }

        @Override
        public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
            return types.copyRecordType(this, mappings);
        }

        @Override
        public SnacksList<Type> getArguments() {
            return arguments;
        }

        @Override
        public Symbol getName() {
            return name;
        }

        public SnacksList<Property> getProperties() {
            return properties;
        }

        @Override
        public int hashCode() {
            return Objects.hash(properties);
        }

        @Override
        public void print(TypePrinter printer) {
            printer.printRecordType(this);
        }

        @Override
        public String toString() {
            return "RecordType{name=" + name + ", arguments=" + arguments + ", properties=" + properties + "}";
        }

        @Snack(name = "Property", kind = TYPE)
        public static final class Property {

            @SnackType
            public static Type propertyType() {
                return record("snacks.lang.Property", asList(
                    property("name", symbolType()),
                    property("type", recur("snacks.lang.Type"))
                ));
            }

            private final Symbol name;
            private final Type type;

            public Property(String name, Type type) {
                this(Symbol.valueOf(name), type);
            }

            public Property(Symbol name, Type type) {
                this.name = name;
                this.type = type;
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                } else if (o instanceof Property) {
                    Property other = (Property) o;
                    return new EqualsBuilder()
                        .append(name, other.name)
                        .append(type, other.type)
                        .isEquals();
                } else {
                    return false;
                }
            }

            public Property expose() {
                return new Property(name, type.expose());
            }

            public Symbol getName() {
                return name;
            }

            public Type getType() {
                return type;
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, type);
            }

            @Override
            public String toString() {
                return "Property{name=" + name + ", type=" + type + "}";
            }
        }
    }

    @Snack(name = "RecursiveType", kind = TYPE)
    public static class RecursiveType extends Type {

        @SnackType
        public static Type recursiveType() {
            return record("snacks.lang.RecursiveType", asList(
                property("name", symbolType()),
                property("arguments", listOf(recur("snacks.lang.Type")))
            ));
        }

        private final Symbol name;
        private final SnacksList<Type> arguments;

        public RecursiveType(String name, Iterable<Type> arguments) {
            this(Symbol.valueOf(name), arguments);
        }

        public RecursiveType(Symbol name, Iterable<Type> arguments) {
            this.name = name;
            this.arguments = toList(arguments);
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
        public void generate(TypeGenerator generator) {
            generator.generateRecursiveType(this);
        }

        @Override
        public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
            return types.copyRecursiveType(this, mappings);
        }

        @Override
        public SnacksList<Type> getArguments() {
            return arguments;
        }

        @Override
        public Symbol getName() {
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
            return "RecursiveType{name=" + name + ", arguments=" + arguments + "}";
        }
    }

    @Snack(name = "SimpleType", kind = TYPE)
    public static class SimpleType extends Type {

        @SnackType
        public static Type simpleType() {
            return record("snacks.lang.SimpleType", asList(
                property("name", symbolType())
            ));
        }

        private final Symbol name;

        public SimpleType(String name) {
            this(Symbol.valueOf(name));
        }

        public SimpleType(Symbol name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof SimpleType && Objects.equals(name, ((SimpleType) o).name);
        }

        @Override
        public Type expose() {
            return this;
        }

        @Override
        public void generate(TypeGenerator generator) {
            generator.generateSimpleType(this);
        }

        @Override
        public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
            return types.copySimpleType(this, mappings);
        }

        @Override
        public Symbol getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public void print(TypePrinter printer) {
            printer.printSimpleType(this);
        }

        @Override
        public String toString() {
            return "SimpleType{name=" + name + "}";
        }
    }

    @Snack(name = "UnionType", kind = TYPE)
    public static class UnionType extends Type {

        private static final Symbol name = Symbol.valueOf("snacks.lang.UnionType");

        @SnackType
        public static Type unionType() {
            return record("snacks.lang.UnionType", asList(
                property("name", symbolType()),
                property("types", listOf(recur("snacks.lang.Type")))
            ));
        }

        private final Set<Type> types;

        public UnionType(Iterable<Type> types) {
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
        public SnacksList<Type> decompose() {
            return toList(types);
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
        public Symbol getName() {
            return name;
        }

        public SnacksList<Type> getTypes() {
            return toList(types);
        }

        @Override
        public int hashCode() {
            return Objects.hash(types);
        }

        @Override
        public void print(TypePrinter printer) {
            printer.printUnionType(this);
        }

        @Override
        public String toString() {
            return "UnionType{types=" + types + "}";
        }
    }

    @Snack(name = "VariableType", kind = TYPE)
    public static class VariableType extends Type {

        @SnackType
        public static Type variableType() {
            return record("snacks.lang.VariableType", asList(
                property("name", symbolType())
            ));
        }

        private State state;

        public VariableType(String name) {
            this(Symbol.valueOf(name));
        }

        public VariableType(Symbol name) {
            this.state = new UnboundState(this, name);
        }

        public VariableType(Type type) {
            this.state = new BoundState(type);
        }

        @Override
        public void bind(Type type) {
            state.bind(type);
        }

        @Override
        public SnacksList<Type> decompose() {
            return state.decompose();
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof VariableType && Objects.equals(state, ((VariableType) o).state);
        }

        @Override
        public Type expose() {
            return state.expose();
        }

        @Override
        public void generate(TypeGenerator generator) {
            generator.generateVariableType(this);
        }

        @Override
        public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
            return types.copyVariableType(this, mappings);
        }

        @Override
        public Symbol getName() {
            return state.getName();
        }

        @Override
        public int hashCode() {
            return Objects.hash(state);
        }

        @Override
        public void print(TypePrinter printer) {
            printer.printVariableType(this);
        }

        @Override
        public Type recompose(Type type, TypeFactory types) {
            return state.recompose(type, types);
        }

        @Override
        public String toString() {
            return state.toString();
        }

        private interface State {

            void bind(Type type);

            SnacksList<Type> decompose();

            Type expose();

            Symbol getName();

            Type recompose(Type functionType, TypeFactory environment);
        }

        private static final class BoundState implements State {

            private final Type type;

            public BoundState(Type type) {
                this.type = type;
            }

            @Override
            public void bind(Type type) {
                // intentionally empty
            }

            @Override
            public SnacksList<Type> decompose() {
                return type.decompose();
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof BoundState && Objects.equals(type, ((BoundState) o).type);
            }

            @Override
            public Type expose() {
                return type.expose();
            }

            @Override
            public Symbol getName() {
                return type.getName();
            }

            @Override
            public int hashCode() {
                return Objects.hash(type);
            }

            @Override
            public Type recompose(Type functionType, TypeFactory environment) {
                return type;
            }

            @Override
            public String toString() {
                return "VariableType{bind=" + type + "}";
            }
        }

        private static final class UnboundState implements State {

            private final VariableType parent;
            private final Symbol name;

            public UnboundState(VariableType parent, Symbol name) {
                this.parent = parent;
                this.name = name;
            }

            @Override
            public void bind(Type type) {
                parent.state = new BoundState(type);
            }

            @Override
            public SnacksList<Type> decompose() {
                return toList((Type) parent);
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof UnboundState && Objects.equals(name, ((UnboundState) o).name);
            }

            @Override
            public Type expose() {
                return parent;
            }

            @Override
            public Symbol getName() {
                return name;
            }

            @Override
            public int hashCode() {
                return Objects.hash(name);
            }

            @Override
            public Type recompose(Type functionType, TypeFactory environment) {
                int size = functionType.decompose().size();
                List<Type> variables = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    variables.add(environment.createVariable());
                }
                return Types.union(variables);
            }

            @Override
            public String toString() {
                return "VariableType{name=" + name + "}";
            }
        }
    }
}
