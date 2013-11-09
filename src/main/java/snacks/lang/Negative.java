package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.DOUBLE_TYPE;
import static snacks.lang.type.Types.INTEGER_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.union;

import snacks.lang.type.Type;

@Snack(name = "unary-", kind = EXPRESSION)
@Prefix(precedence = 15)
public class Negative implements _Function {

    private static Negative instance;

    public static Negative instance() {
        if (instance == null) {
            instance = new Negative();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(
            func(INTEGER_TYPE, INTEGER_TYPE),
            func(DOUBLE_TYPE, DOUBLE_TYPE)
        );
    }

    public Integer apply(Integer argument) {
        return -argument;
    }

    public Double apply(Double argument) {
        return -argument;
    }
}
