package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.BOOLEAN_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import java.util.Objects;
import snacks.lang.type.Type;

@Snack(name = "!=", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 6)
public class NotEquals implements _Function {

    private static NotEquals instance;

    public static NotEquals instance() {
        if (instance == null) {
            instance = new NotEquals();
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
            return !Objects.equals(left, right);
        }
    }
}
