package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.VOID_TYPE;

import snacks.lang.type.Type;

@Snack(name = "Void", kind = TYPE)
@JavaType(Void.class)
public class SnacksVoid {

    @SnackType
    public static final Type type = VOID_TYPE;
}
