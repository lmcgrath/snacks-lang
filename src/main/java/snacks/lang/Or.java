package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.Type.BOOLEAN_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.set;

@Snack("or")
@Infix(fixity = LEFT, precedence = 3, shortCircuit = true)
public class Or {

    private static Or instance;

    public static Or instance() {
        if (instance == null) {
            instance = new Or();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return set(func(BOOLEAN_TYPE, func(BOOLEAN_TYPE, BOOLEAN_TYPE)));
    }

    public Closure apply(Boolean left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Boolean left;

        public Closure(Boolean left) {
            this.left = left;
        }

        public Boolean apply(Boolean right) {
            return left || right;
        }
    }
}
