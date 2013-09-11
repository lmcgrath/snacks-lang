package snacks.lang;

import static snacks.lang.Type.DOUBLE_TYPE;

@Snack("Double")
@JavaType(Double.class)
public class SnacksDouble {

    @SnackType
    public static final Type type = DOUBLE_TYPE;
}
