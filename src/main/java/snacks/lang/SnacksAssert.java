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
        return func(BOOLEAN_TYPE, VOID_TYPE);
    }

    public Object apply(Boolean argument) {
        if (!argument) {
            throw new SnacksException("Failed assertion");
        }
        return null;
    }
}
