package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.integerType;

@Snack(name = "Integer", kind = TYPE)
@JavaType(Integer.class)
public class SnacksInteger {

    @SnackType
    public static Type type() {
        return integerType();
    }
}
