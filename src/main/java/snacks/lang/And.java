package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;

@Snack(name = "and", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 4)
public class And {

    private static And instance;

    public static Object instance() {
        if (instance == null) {
            instance = new And();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return Types.union(func(booleanType(), func(booleanType(), booleanType())));
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
