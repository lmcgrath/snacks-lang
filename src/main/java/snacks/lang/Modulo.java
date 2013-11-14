package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.Types.integerType;

@Snack(name = "%", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 13)
public class Modulo {

    private static Modulo instance;

    public static Modulo instance() {
        if (instance == null) {
            instance = new Modulo();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(integerType(), func(integerType(), integerType()));
    }

    public Closure apply(Integer left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Integer left;

        public Closure(Integer left) {
            this.left = left;
        }

        public Integer apply(Integer right) {
            return left % right;
        }
    }
}
