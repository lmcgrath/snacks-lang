package snacks.lang.reflect;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.*;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.*;
import snacks.lang.type.RecordType.Property;
import snacks.lang.type.Type;

@Snack(name = "Type", kind = TYPE)
public abstract class TypeInfo {

    @SnackType
    public static Type type() {
        return algebraic("snacks.lang.reflect.Type", asList(
            SimpleInfo.type(),
            FunctionInfo.type(),
            VariableInfo.type(),
            RecordInfo.type(),
            AlgebraicInfo.type()
        ));
    }

    private static Type typeList(Type argument) {
        return algebraic("snacks.lang.List", asList(argument), asList(
            simple("snacks.lang.EmptyList"),
            record("snacks.lang.ListElement", asList(argument), asList(
                property("_0", argument),
                property("_1", recur("snacks.lang.List", asList(argument)))
            ))
        ));
    }

    private static Property typeParent() {
        return property("parent", algebraic("snacks.lang.Maybe", asList(SYMBOL_TYPE), asList(
            simple("snacks.lang.Nothing"),
            record("snacks.lang.Just", asList(SYMBOL_TYPE), asList(
                property("_0", SYMBOL_TYPE)
            ))
        )));
    }

    private TypeInfo() {
        // intentionally empty
    }

    @Snack(name = "SimpleType", kind = EXPRESSION)
    public static final class SimpleConstructor {

        private static SimpleConstructor instance;

        public static SimpleConstructor instance() {
            if (instance == null) {
                instance = new SimpleConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(SYMBOL_TYPE, func(typeParent().getType(), SimpleInfo.type()));
        }

        public Object apply(Object name) {
            return new Closure((Symbol) name);
        }

        public static final class Closure {

            private final Symbol name;

            public Closure(Symbol name) {
                this.name = name;
            }

            public Object apply(Object parent) {
                return new SimpleInfo(name, (Maybe) parent);
            }
        }
    }

    @Snack(name = "SimpleType", kind = TYPE)
    public static final class SimpleInfo extends TypeInfo {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.SimpleType", asList(
                property("name", SYMBOL_TYPE),
                typeParent()
            ));
        }

        private final Symbol name;
        private final Maybe parent;

        public SimpleInfo(Symbol name, Maybe parent) {
            this.name = name;
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof SimpleInfo) {
                SimpleInfo other = (SimpleInfo) o;
                return new EqualsBuilder()
                    .append(name, other.name)
                    .append(parent, other.parent)
                    .isEquals();
            } else {
                return false;
            }
        }

        public Symbol getName() {
            return name;
        }

        public Maybe getParent() {
            return parent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parent);
        }

        @Override
        public String toString() {
            return "SimpleType{name=" + name + ", parent=" + parent + "}";
        }
    }

    @Snack(name = "FunctionType", kind = EXPRESSION)
    public static final class FunctionConstructor {

        private static FunctionConstructor instance;

        private static FunctionConstructor instance() {
            if (instance == null) {
                instance = new FunctionConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(SYMBOL_TYPE, func(SYMBOL_TYPE, FunctionInfo.type()));
        }

        public Object apply(Object argument) {
            return new Closure((Symbol) argument);
        }

        public static final class Closure {

            private final Symbol argument;

            public Closure(Symbol argument) {
                this.argument = argument;
            }

            public Object apply(Symbol result) {
                return new FunctionInfo(argument, result);
            }
        }
    }

    @Snack(name = "FunctionType", kind = TYPE)
    public static final class FunctionInfo extends TypeInfo {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.FunctionType", asList(
                property("argument", SYMBOL_TYPE),
                property("result", SYMBOL_TYPE)
            ));
        }

        private final Symbol argument;
        private final Symbol result;

        public FunctionInfo(Symbol argument, Symbol result) {
            this.argument = argument;
            this.result = result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof FunctionInfo) {
                FunctionInfo other = (FunctionInfo) o;
                return new EqualsBuilder()
                    .append(argument, other.argument)
                    .append(result, other.result)
                    .isEquals();
            } else {
                return false;
            }
        }

        public Symbol getArgument() {
            return argument;
        }

        public Symbol getResult() {
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(argument, result);
        }

        @Override
        public String toString() {
            return "FunctionType{argument=" + argument + ", result=" + result + "}";
        }
    }

    @Snack(name = "VariableType", kind = EXPRESSION)
    public static final class VariableConstructor {

        private static VariableConstructor instance;

        public static VariableConstructor instance() {
            if (instance == null) {
                instance = new VariableConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(SYMBOL_TYPE, VariableInfo.type());
        }

        public Object apply(Object name) {
            return new VariableInfo((Symbol) name);
        }
    }

    @Snack(name = "VariableType", kind = TYPE)
    public static final class VariableInfo extends TypeInfo {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.VariableType", asList(
                property("name", SYMBOL_TYPE)
            ));
        }

        private final Symbol name;

        public VariableInfo(Symbol name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof VariableInfo && Objects.equals(name, ((VariableInfo) o).name);
        }

        public Symbol getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "VariableType{name=" + name + "}";
        }
    }

    @Snack(name = "RecordType", kind = EXPRESSION)
    public static final class RecordConstructor {

        private static RecordConstructor instance;

        public static RecordConstructor instance() {
            if (instance == null) {
                instance = new RecordConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(SYMBOL_TYPE,
                func(typeList(SYMBOL_TYPE),
                    func(typeList(tuple(SYMBOL_TYPE, SYMBOL_TYPE)),
                        func(typeParent().getType(), RecordInfo.type()))));
        }

        public Object apply(Object name) {
            return new Closure1((Symbol) name);
        }

        public static final class Closure1 {

            private final Symbol name;

            public Closure1(Symbol name) {
                this.name = name;
            }

            public Object apply(Object arguments) {
                return new Closure2(name, (ListType) arguments);
            }
        }

        public static final class Closure2 {

            private final Symbol name;
            private final ListType arguments;

            public Closure2(Symbol name, ListType arguments) {
                this.name = name;
                this.arguments = arguments;
            }

            public Object apply(ListType properties) {
                return new Closure3(name, arguments, properties);
            }
        }

        public static final class Closure3 {

            private final Symbol name;
            private final ListType arguments;
            private final ListType properties;

            public Closure3(Symbol name, ListType arguments, ListType properties) {
                this.name = name;
                this.arguments = arguments;
                this.properties = properties;
            }

            public Object apply(Object parent) {
                return new RecordInfo(name, arguments, properties, (Maybe) parent);
            }
        }
    }

    @Snack(name = "RecordType", kind = TYPE)
    public static final class RecordInfo extends TypeInfo {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.RecordType", asList(
                property("name", SYMBOL_TYPE),
                property("arguments", typeList(SYMBOL_TYPE)),
                property("properties", typeList(tuple(SYMBOL_TYPE, SYMBOL_TYPE))),
                typeParent()
            ));
        }

        private final Symbol name;
        private final ListType arguments;
        private final ListType properties;
        private final Maybe parent;

        public RecordInfo(Symbol name, ListType arguments, ListType properties, Maybe parent) {
            this.name = name;
            this.arguments = arguments;
            this.properties = properties;
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof RecordInfo) {
                RecordInfo other = (RecordInfo) o;
                return new EqualsBuilder()
                    .append(name, other.name)
                    .append(arguments, other.arguments)
                    .append(properties, other.properties)
                    .append(parent, other.parent)
                    .isEquals();
            } else {
                return false;
            }
        }

        public ListType getArguments() {
            return arguments;
        }

        public Symbol getName() {
            return name;
        }

        public Maybe getParent() {
            return parent;
        }

        public ListType getProperties() {
            return properties;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, arguments, properties, parent);
        }

        @Override
        public String toString() {
            return "RecordType{name=" + name + ", arguments=" + arguments + ", properties=" + properties + ", parent=" + parent + "}";
        }
    }

    @Snack(name = "AlgebraicType", kind = EXPRESSION)
    public static final class AlgebraicConstructor {

        private static AlgebraicConstructor instance;

        public static AlgebraicConstructor instance() {
            if (instance == null) {
                instance = new AlgebraicConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(SYMBOL_TYPE, func(typeList(SYMBOL_TYPE), typeList(SYMBOL_TYPE)));
        }

        public Object apply(Object name) {
            return new Closure1((Symbol) name);
        }

        public static final class Closure1 {

            private final Symbol name;

            public Closure1(Symbol name) {
                this.name = name;
            }

            public Object apply(Object arguments) {
                return new Closure2(name, (ListType) arguments);
            }
        }

        public static final class Closure2 {

            private final Symbol name;
            private final ListType arguments;

            public Closure2(Symbol name, ListType arguments) {
                this.name = name;
                this.arguments = arguments;
            }

            public Object apply(Object members) {
                return new AlgebraicInfo(name, arguments, (ListType) members);
            }
        }
    }

    @Snack(name = "AlgebraicType", kind = TYPE)
    public static final class AlgebraicInfo extends TypeInfo {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.AlgebraicType", asList(
                property("name", SYMBOL_TYPE),
                property("arguments", typeList(SYMBOL_TYPE)),
                property("members", typeList(SYMBOL_TYPE))
            ));
        }

        private final Symbol name;
        private final ListType arguments;
        private final ListType members;

        public AlgebraicInfo(Symbol name, ListType arguments, ListType members) {
            this.name = name;
            this.arguments = arguments;
            this.members = members;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof AlgebraicInfo) {
                AlgebraicInfo other = (AlgebraicInfo) o;
                return new EqualsBuilder()
                    .append(name, other.name)
                    .append(arguments, other.arguments)
                    .append(members, other.members)
                    .isEquals();
            } else {
                return false;
            }
        }

        public ListType getArguments() {
            return arguments;
        }

        public ListType getMembers() {
            return members;
        }

        public Symbol getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, arguments, members);
        }

        @Override
        public String toString() {
            return "AlgebraicType{name=" + name + ", arguments=" + arguments + ", members=" + members + "}";
        }
    }
}
