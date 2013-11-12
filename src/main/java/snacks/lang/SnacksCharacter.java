package snacks.lang;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.characterType;

@Snack(name = "Character", kind = TYPE)
@JavaType(Character.class)
public class SnacksCharacter {

    @SnackType
    public static Type type() {
        return characterType();
    }
}
