package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.booleanType;

@Snack(name = "Boolean", kind = TYPE)
@JavaType(Boolean.class)
public class SnacksBoolean {

    @SnackType
    public static Type type() {
        return booleanType();
    }
}
