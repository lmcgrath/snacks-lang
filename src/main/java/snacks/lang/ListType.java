package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.*;

import snacks.lang.type.Type;

@Snack(name = "List", kind = TYPE, arguments = "snacks.lang.List#a")
public abstract class ListType {

    @SnackType
    public static Type type() {
        return algebraic("snacks.lang.List", asList(var("snacks.lang.List#a")), asList(
            simple("snacks.lang.EmptyList"),
            record("snacks.lang.ListElement", asList(var("snacks.lang.List#a")), asList(
                property("_0", var("snacks.lang.List#a")),
                property("_1", recur("snacks.lang.List", asList(var("snacks.lang.List#a"))))
            ))
        ));
    }

    private ListType() {
        // intentionally empty
    }

    @Snack(name = "ListElement", kind = EXPRESSION)
    public static final class ListElementConstructor {

        private static ListElementConstructor instance;

        public static ListElementConstructor instance() {
            if (instance == null) {
                instance = new ListElementConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(var("snacks.lang.List#a"), func(ListType.type(), ListElement.type()));
        }

        public Object apply(Object value) {
            return new Closure(value);
        }

        public static final class Closure {

            private final Object value;

            public Closure(Object value) {
                this.value = value;
            }

            public Object apply(Object tail) {
                return new ListElement(value, (ListType) tail);
            }
        }
    }

    @Snack(name = "ListElement", kind = TYPE, arguments = "snacks.lang.List#a")
    public static final class ListElement extends ListType {

        @SnackType
        public static Type type() {
            return record("snacks.lang.ListElement", asList(var("snacks.lang.List#a")), asList(
                property("_0", var("snacks.lang.List#a")),
                property("_1", algebraic("snacks.lang.List", asList(var("snacks.lang.List#a")), asList(
                    simple("snacks.lang.EmptyList"),
                    recur("snacks.lang.ListElement", asList(var("snacks.lang.List#a")))
                )))
            ));
        }

        private final Object _0;
        private final ListType _1;

        public ListElement(Object _0, ListType _1) {
            this._0 = _0;
            this._1 = _1;
        }

        public Object get_0() {
            return _0;
        }

        public ListType get_1() {
            return _1;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append(_0);
            ListType remainder = _1;
            if (!(remainder instanceof EmptyList)) {
                while (!(remainder instanceof EmptyList)) {
                    builder.append(", ");
                    ListElement tail = (ListElement) remainder;
                    builder.append(tail._0);
                    remainder = tail._1;
                }
            }
            builder.append("]");
            return builder.toString();
        }
    }

    @Snack(name = "EmptyList", kind = EXPRESSION)
    public static final class EmptyListConstructor {

        public static Object instance() {
            return EmptyList.value();
        }

        @SnackType
        public static Type type() {
            return EmptyList.type();
        }
    }

    @Snack(name = "EmptyList", kind = TYPE)
    public static final class EmptyList extends ListType {

        private static EmptyList value;

        public static EmptyList value() {
            if (value == null) {
                value = new EmptyList();
            }
            return value;
        }

        @SnackType
        public static Type type() {
            return simple("snacks.lang.EmptyList");
        }

        @Override
        public String toString() {
            return "[]";
        }
    }
}
