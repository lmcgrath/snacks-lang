package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.CHARACTER_TYPE;

import snacks.lang.type.Type;

@Snack(name = "Character", kind = TYPE)
@JavaType(Character.class)
public class SnacksCharacter {

    @SnackType
    public static final Type type = CHARACTER_TYPE;
}
