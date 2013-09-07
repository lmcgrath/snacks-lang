package snacks.lang;

import static snacks.lang.Type.STRING_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.var;

@Snack("stringy")
public class Stringy {

    private static Stringy instance;

    public static Stringy instance() {
        if (instance == null) {
            instance = new Stringy();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), STRING_TYPE);
    }

    public String apply(Object argument) {
        return argument.toString();
    }
}
