package snacks.lang;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static snacks.lang.type.Types.BOOLEAN_TYPE;
import static snacks.lang.type.Types.func;

import snacks.lang.type.Type;

@Snack(name = "not")
@Prefix(precedence = 5)
public class Not {

    private static Not instance;

    public static Not instance() {
        if (instance == null) {
            instance = new Not();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(BOOLEAN_TYPE, BOOLEAN_TYPE);
    }

    public Boolean apply(Boolean argument) {
        return argument ? FALSE : TRUE;
    }
}
