package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.Types.stringType;
import static snacks.lang.Types.var;

@Snack(name = "concat", kind = EXPRESSION)
public class Concat {

    private static Concat instance;

    public static Concat instance() {
        if (instance == null) {
            instance = new Concat();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), func(var("U"), stringType()));
    }

    public ConcatClosure apply(Object left) {
        return new ConcatClosure(left);
    }

    public static final class ConcatClosure {

        private final Object left;

        public ConcatClosure(Object left) {
            this.left = left;
        }

        public String apply(Object right) {
            return left.toString() + right.toString();
        }
    }
}
