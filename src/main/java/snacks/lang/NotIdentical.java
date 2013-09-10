package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.Type.BOOLEAN_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.var;

@Snack("is not")
@Infix(fixity = LEFT, precedence = 6)
public class NotIdentical {

    private static Identical instance;

    public static Identical instance() {
        if (instance == null) {
            instance = new Identical();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), func(var("U"), BOOLEAN_TYPE));
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
            return left != right;
        }
    }
}
