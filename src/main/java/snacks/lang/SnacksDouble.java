package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.doubleType;

@Snack(name = "Double", kind = TYPE)
@JavaType(Double.class)
public class SnacksDouble {

    @SnackType
    public static Type type() {
        return doubleType();
    }
}
