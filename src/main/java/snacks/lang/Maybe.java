package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.*;

import snacks.lang.type.Type;

@Snack(name = "Maybe", kind = TYPE, parameters = "a")
public interface Maybe {

    @Snack(name = "Just", kind = EXPRESSION)
    static final class JustConstructor {

        private static JustConstructor instance;

        public static JustConstructor instance() {
            if (instance == null) {
                instance = new JustConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(var("a"), record("snacks.lang.Just", asList(
                property("_0", var("snacks.lang.Maybe#a"))
            )));
        }

        public Just apply(Object a) {
            return new Just(a);
        }
    }

    @Snack(name = "Just", kind = TYPE)
    static final class Just extends Tuple1 implements Maybe {

        @SnackType
        public static Type type() {
            return record("snacks.lang.Just", asList(
                property("_0", var("snacks.lang.Maybe#a"))
            ));
        }

        public Just(Object _0) {
            super(_0);
        }
    }

    @Snack(name = "Nothing", kind = { EXPRESSION, TYPE })
    static final class Nothing implements Maybe {

        private static Object instance;

        public static Object instance() {
            if (instance == null) {
                instance = new Nothing();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return simple("snacks.lang.Nothing");
        }
    }
}
