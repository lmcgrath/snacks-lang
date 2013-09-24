package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.Type.BOOLEAN_TYPE;
import static snacks.lang.Type.VOID_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.set;

@Snack("and")
@Infix(fixity = LEFT, precedence = 4, shortCircuit = true)
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
        return set(
            func(BOOLEAN_TYPE, func(BOOLEAN_TYPE, BOOLEAN_TYPE)),
            func(func(VOID_TYPE, BOOLEAN_TYPE), func(func(VOID_TYPE, BOOLEAN_TYPE), BOOLEAN_TYPE))
        );
    }

    public LazyClosure apply(Invokable<Boolean> left) {
        return new LazyClosure(left);
    }

    public StrictClosure apply(Boolean left) {
        return new StrictClosure(left);
    }

    public static final class LazyClosure {

        private final Invokable<Boolean> left;

        public LazyClosure(Invokable<Boolean> left) {
            this.left = left;
        }

        public Boolean apply(Invokable<Boolean> right) {
            return left.invoke() && right.invoke();
        }
    }

    public static final class StrictClosure {

        private final Boolean left;

        public StrictClosure(Boolean left) {
            this.left = left;
        }

        public Boolean apply(Boolean right) {
            return left && right;
        }
    }
}
