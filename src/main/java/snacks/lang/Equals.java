package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.type.Types.BOOLEAN_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import java.util.Objects;
import snacks.lang.type.Type;

@Snack(name = "==")
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
        return func(var("T"), func(var("U"), BOOLEAN_TYPE));
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
