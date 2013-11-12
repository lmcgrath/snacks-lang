package snacks.lang;

import static java.lang.Thread.currentThread;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.Types.var;

import snacks.lang.runtime.SnacksClassLoader;

@Snack(name = "typeOf", kind = EXPRESSION)
public class TypeOf {

    private static TypeOf instance;

    public static TypeOf instance() {
        if (instance == null) {
            instance = new TypeOf();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("snacks.lang.typeOf#a"), Type.type());
    }

    public Object apply(Object expression) {
        return ((SnacksClassLoader) currentThread().getContextClassLoader()).typeOf(expression.getClass()).require();
    }
}
