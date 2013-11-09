package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.property;
import static snacks.lang.type.Types.record;
import static snacks.lang.type.Types.simple;
import static snacks.lang.type.Types.var;

import snacks.lang.type.Type;

@Snack(name = "Maybe", kind = TYPE, arguments = "snacks.lang.Maybe#a")
public abstract class Maybe {

    private Maybe() {
        // intentionally empty
    }

    @Snack(name = "Just", kind = EXPRESSION)
    public static final class JustConstructor implements _Function {

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
    public static final class Just extends Maybe implements _Record {

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

        public Object get_0() {
            return _0;
        }

        @Override
        public String toString() {
            return "Just(" + _0 + ")";
        }
    }

    @Snack(name = "Nothing", kind = EXPRESSION)
    public static final class NothingConstructor implements _Constant {

        @SnackType
        public static Type type() {
            return simple("snacks.lang.Nothing");
        }

        public static Object instance() {
            return Nothing.value();
        }
    }

    @Snack(name = "Nothing", kind = TYPE)
    public static final class Nothing extends Maybe implements _Constant {

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
        public String toString() {
            return "Nothing";
        }
    }
}
