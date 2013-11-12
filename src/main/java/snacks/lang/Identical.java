package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.var;

@Snack(name = "is", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 6)
public class Identical {

    private static Identical instance;

    public static Identical instance() {
        if (instance == null) {
            instance = new Identical();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), func(var("U"), booleanType()));
    }

    public Closure apply(Object left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Object left;

        public Closure(Object left) {
            this.left = left;
        }

        public Boolean apply(Object right) {
            return left == right;
        }
    }
}
