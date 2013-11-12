package snacks.lang;

import static java.lang.System.out;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.VOID_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import snacks.lang.type.Type;

@Snack(name = "say", kind = EXPRESSION)
public class Say {

    private static Say instance;

    public static Say instance() {
        if (instance == null) {
            instance = new Say();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), VOID_TYPE);
    }

    public Object apply(Boolean value) {
        out.println(value.booleanValue());
        return null;
    }

    public Object apply(Double value) {
        out.println(value.doubleValue());
        return null;
    }

    public Object apply(Integer value) {
        out.println(value.intValue());
        return null;
    }

    public Object apply(String value) {
        out.println(value);
        return null;
    }

    public Object apply(Object value) {
        out.println(value);
        return null;
    }
}
