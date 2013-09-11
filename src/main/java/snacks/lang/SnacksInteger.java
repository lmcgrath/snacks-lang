package snacks.lang;

import static snacks.lang.Type.INTEGER_TYPE;

@Snack("Integer")
@JavaType(Integer.class)
public class SnacksInteger {

    @SnackType
    public static final Type type = INTEGER_TYPE;
}
