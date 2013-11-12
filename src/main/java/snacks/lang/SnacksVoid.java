package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.voidType;

@Snack(name = "Void", kind = TYPE)
@JavaType(Void.class)
public class SnacksVoid {

    @SnackType
    public static Type type() {
        return voidType();
    }
}
