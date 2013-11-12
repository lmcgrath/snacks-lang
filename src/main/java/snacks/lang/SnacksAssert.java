package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.voidType;
import static snacks.lang.Types.func;

@Snack(name = "assert", kind = EXPRESSION)
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
        return func(booleanType(), voidType());
    }

    public Object apply(Boolean argument) {
        if (!argument) {
            throw new SnacksException("Failed assertion");
        }
        return null;
    }
}
