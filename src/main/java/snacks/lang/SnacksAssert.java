package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.BOOLEAN_TYPE;
import static snacks.lang.type.Types.VOID_TYPE;
import static snacks.lang.type.Types.func;

import snacks.lang.type.Type;

@Snack(name = "assert", kind = EXPRESSION)
public class SnacksAssert implements _Function {

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
