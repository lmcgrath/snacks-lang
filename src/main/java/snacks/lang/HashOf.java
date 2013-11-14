package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.Types.integerType;
import static snacks.lang.Types.var;

@Snack(name = "hashOf", kind = EXPRESSION)
public class HashOf {

    private static HashOf instance;

    public static HashOf instance() {
        if (instance == null) {
            instance = new HashOf();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("snacks.lang.hashOf#a"), integerType());
    }

    public Object apply(Object expression) {
        return expression.hashCode();
    }
}
