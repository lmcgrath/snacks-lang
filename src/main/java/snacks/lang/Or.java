package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.BOOLEAN_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.union;

import snacks.lang.type.Type;

@Snack(name = "or", kind = EXPRESSION)
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
        return union(func(BOOLEAN_TYPE, func(BOOLEAN_TYPE, BOOLEAN_TYPE)));
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
