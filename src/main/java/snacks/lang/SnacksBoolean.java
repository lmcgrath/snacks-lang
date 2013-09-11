package snacks.lang;

import static snacks.lang.Type.BOOLEAN_TYPE;

@Snack("Boolean")
@JavaType(Boolean.class)
public class SnacksBoolean {

    @SnackType
    public static final Type type = BOOLEAN_TYPE;
}
