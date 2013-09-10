package snacks.lang;

import static snacks.lang.Fixity.RIGHT;
import static snacks.lang.Type.DOUBLE_TYPE;
import static snacks.lang.Type.INTEGER_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.set;

@Snack("unary-")
@Affix(fixity = RIGHT, precedence = 15)
public class Negative {

    private static Negative instance;

    public static Negative instance() {
        if (instance == null) {
            instance = new Negative();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return set(
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
