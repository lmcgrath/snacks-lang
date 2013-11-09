package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.STRING_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import snacks.lang.type.Type;

@Snack(name = "string", kind = EXPRESSION)
public class StringConstructor {

    private static StringConstructor instance;

    public static StringConstructor instance() {
        if (instance == null) {
            instance = new StringConstructor();
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
