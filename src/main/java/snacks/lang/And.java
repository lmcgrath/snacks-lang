package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.Type.BOOLEAN_TYPE;
import static snacks.lang.Type.func;

@Snack("and")
@Infix(fixity = LEFT, precedence = 4)
public class And {

    private static And instance;

    public static And instance() {
        if (instance == null) {
            instance = new And();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(BOOLEAN_TYPE, func(BOOLEAN_TYPE, BOOLEAN_TYPE));
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
            return left && right;
        }
    }
}
