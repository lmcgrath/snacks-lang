package snacks.lang.reflect;

import static java.lang.Thread.currentThread;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.reflect.TypeTransformer.transform;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.runtime.SnacksClassLoader;
import snacks.lang.type.Type;

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
        return func(var("snacks.lang.typeOf#a"), TypeInfo.type());
    }

    public Object apply(Object expression) {
        return transform(typeOf(expression));
    }

    private Type typeOf(Object expression) {
        return ((SnacksClassLoader) currentThread().getContextClassLoader()).typeOf(expression.getClass()).require();
    }
}
