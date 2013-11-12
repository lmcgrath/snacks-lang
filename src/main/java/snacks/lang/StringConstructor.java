package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.stringType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.var;

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
        return func(var("T"), stringType());
    }

    public String apply(Object argument) {
        return argument.toString();
    }
}
