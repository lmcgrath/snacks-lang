package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.DOUBLE_TYPE;

import snacks.lang.type.Type;

@Snack(name = "Double", kind = TYPE)
@JavaType(Double.class)
public class SnacksDouble {

    @SnackType
    public static final Type type = DOUBLE_TYPE;
}
