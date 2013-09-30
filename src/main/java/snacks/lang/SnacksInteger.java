package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.INTEGER_TYPE;

import snacks.lang.type.Type;

@Snack(name = "Integer", kind = TYPE)
@JavaType(Integer.class)
public class SnacksInteger {

    @SnackType
    public static final Type type = INTEGER_TYPE;
}
