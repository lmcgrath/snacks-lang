package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.Types.symbolType;
import static snacks.lang.Types.var;

@Snack(name = "classOf", kind = EXPRESSION)
public class ClassOf {

    private static ClassOf instance;

    public static ClassOf instance() {
        if (instance == null) {
            instance = new ClassOf();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("snacks.lang.classOf#a"), symbolType());
    }

    public Object apply(Object expression) {
        return Symbol.valueOf(expression.getClass().getName());
    }
}
