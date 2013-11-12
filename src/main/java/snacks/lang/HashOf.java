package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.INTEGER_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import snacks.lang.type.Type;

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
        return func(var("snacks.lang.hashOf#a"), INTEGER_TYPE);
    }

    public Object apply(Object expression) {
        return expression.hashCode();
    }
}
