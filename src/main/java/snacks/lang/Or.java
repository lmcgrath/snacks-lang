package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.union;

@Snack(name = "or", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 3, shortCircuit = true)
public class Or {

    private static Or instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Or();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(func(booleanType(), func(booleanType(), booleanType())));
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
