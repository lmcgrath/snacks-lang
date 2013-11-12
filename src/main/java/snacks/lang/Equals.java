package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.booleanType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.var;

import java.util.Objects;

@Snack(name = "==", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 6)
public class Equals {

    private static Equals instance;

    public static Equals instance() {
        if (instance == null) {
            instance = new Equals();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), func(var("U"), booleanType()));
    }

    public EqualsClosure apply(Object left) {
        return new EqualsClosure(left);
    }

    public static final class EqualsClosure {

        private final Object left;

        public EqualsClosure(Object left) {
            this.left = left;
        }

        public Boolean apply(Object right) {
            return Objects.equals(left, right);
        }
    }
}
