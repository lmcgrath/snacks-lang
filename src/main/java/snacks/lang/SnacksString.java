package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.stringType;

@Snack(name = "String", kind = TYPE)
@JavaType(String.class)
public class SnacksString {

    @SnackType
    public static Type type() {
        return stringType();
    }
}
