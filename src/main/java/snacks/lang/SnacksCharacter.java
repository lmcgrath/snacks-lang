package snacks.lang;

import static snacks.lang.Type.CHARACTER_TYPE;

@Snack("Character")
@JavaType(Character.class)
public class SnacksCharacter {

    @SnackType
    public static final Type type = CHARACTER_TYPE;
}
