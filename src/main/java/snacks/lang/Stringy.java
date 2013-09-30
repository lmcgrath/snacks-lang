package snacks.lang;

import static snacks.lang.type.Types.STRING_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import snacks.lang.type.Type;

@Snack(name = "stringy")
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
