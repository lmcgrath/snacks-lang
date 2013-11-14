package snacks.lang;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;

@Snack(name = "not", kind = EXPRESSION)
@Prefix(precedence = 5)
public class Not {

    private static Not instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Not();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(booleanType(), booleanType());
    }

    public Boolean apply(Boolean argument) {
        return argument ? FALSE : TRUE;
    }
}
