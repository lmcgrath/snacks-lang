package snacks.lang;

import static snacks.lang.Type.*;

@Snack("assert")
public class SnacksAssert {

    private static SnacksAssert instance;

    public static SnacksAssert instance() {
        if (instance == null) {
            instance = new SnacksAssert();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return set(
            func(BOOLEAN_TYPE, VOID_TYPE),
            func(tuple(BOOLEAN_TYPE, STRING_TYPE), VOID_TYPE)
        );
    }

    public Object apply(Boolean argument) {
        if (!argument) {
            throw new SnacksException("Failed assertion");
        }
        return null;
    }

    public Object apply(Tuple2<Boolean, String> arguments) {
        if (!arguments.get_0()) {
            throw new SnacksException("Failed assertion: " + arguments.get_1());
        }
        return null;
    }
}
