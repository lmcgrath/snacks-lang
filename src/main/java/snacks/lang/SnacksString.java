package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.func;
import static snacks.lang.Types.stringType;
import static snacks.lang.Types.var;

@Snack(name = "String", kind = TYPE)
@JavaType(String.class)
public class SnacksString {

    @SnackType
    public static Type type() {
        return stringType();
    }

    @Snack(name = "string", kind = EXPRESSION)
    public static final class Constructor {

        private static Constructor instance;

        public static Object instance() {
            if (instance == null) {
                instance = new Constructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(var("T"), stringType());
        }

        public String apply(Object argument) {
            return argument.toString();
        }
    }
}
