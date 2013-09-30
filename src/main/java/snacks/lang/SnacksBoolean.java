package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.BOOLEAN_TYPE;

import snacks.lang.type.Type;

@Snack(name = "Boolean", kind = TYPE)
@JavaType(Boolean.class)
public class SnacksBoolean {

    @SnackType
    public static final Type type = BOOLEAN_TYPE;
}
