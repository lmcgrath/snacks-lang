package snacks.lang;

import static snacks.lang.Type.VOID_TYPE;

@Snack("Void")
@JavaType(Void.class)
public class SnacksVoid {

    @SnackType
    public static final Type type = VOID_TYPE;
}
