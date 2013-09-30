package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.STRING_TYPE;

import snacks.lang.type.Type;

@Snack(name = "String", kind = TYPE)
@JavaType(String.class)
public class SnacksString {

    @SnackType
    public static final Type type = STRING_TYPE;
}
