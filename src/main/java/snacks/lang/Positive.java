package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.doubleType;
import static snacks.lang.Types.integerType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.union;

@Snack(name = "unary+", kind = EXPRESSION)
@Prefix(precedence = 15)
public class Positive {

    private static Positive instance;

    public static Positive instance() {
        if (instance == null) {
            instance = new Positive();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(
            func(integerType(), integerType()),
            func(doubleType(), doubleType())
        );
    }

    public Integer apply(Integer argument) {
        return +argument;
    }

    public Double apply(Double argument) {
        return +argument;
    }
}
