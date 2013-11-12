package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.func;
import static snacks.lang.Types.property;
import static snacks.lang.Types.record;
import static snacks.lang.Types.simple;
import static snacks.lang.Types.var;

import java.util.Objects;

@Snack(name = "Maybe", kind = TYPE, arguments = "snacks.lang.Maybe#a")
public abstract class Maybe {

    private Maybe() {
        // intentionally empty
    }

    public abstract <T> T require();

    @Snack(name = "Just", kind = EXPRESSION)
    public static final class JustConstructor {

        private static JustConstructor instance;

        public static JustConstructor instance() {
            if (instance == null) {
                instance = new JustConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(var("snacks.lang.Maybe#a"), record("snacks.lang.Just", asList(var("snacks.lang.Maybe#a")), asList(
                property("_0", var("snacks.lang.Maybe#a"))
            )));
        }

        public Object apply(Object value) {
            return new Just(value);
        }
    }

    @Snack(name = "Just", kind = TYPE, arguments = "snacks.lang.Maybe#a")
    public static final class Just extends Maybe {

        @SnackType
        public static Type type() {
            return record("snacks.lang.Just", asList(var("snacks.lang.Maybe#a")), asList(
                property("_0", var("snacks.lang.Maybe#a"))
            ));
        }

        private final Object _0;

        public Just(Object _0) {
            this._0 = _0;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof Just && Objects.equals(_0, ((Just) o)._0);
        }

        public Object get_0() {
            return _0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_0);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T require() {
            return (T) _0;
        }

        @Override
        public String toString() {
            return "Just(" + _0 + ")";
        }
    }

    @Snack(name = "Nothing", kind = EXPRESSION)
    public static final class NothingConstructor {

        @SnackType
        public static Type type() {
            return simple("snacks.lang.Nothing");
        }

        public static Object instance() {
            return Nothing.value();
        }
    }

    @Snack(name = "Nothing", kind = TYPE)
    public static final class Nothing extends Maybe {

        private static Nothing instance;

        public static Nothing value() {
            if (instance == null) {
                instance = new Nothing();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return simple("snacks.lang.Nothing");
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof Nothing;
        }

        @Override
        public int hashCode() {
            return Objects.hash();
        }

        @Override
        public <T> T require() {
            throw new SnacksException("Can't get something out of Nothing!");
        }

        @Override
        public String toString() {
            return "Nothing";
        }
    }
}
