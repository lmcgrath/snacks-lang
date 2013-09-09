package snacks.lang;

import static org.apache.commons.lang.reflect.MethodUtils.getMatchingAccessibleMethod;
import static snacks.lang.Fixity.RIGHT;
import static snacks.lang.Type.func;
import static snacks.lang.Type.var;

import java.lang.reflect.Method;

@Snack("$")
@Infix(fixity = RIGHT, precedence = 1)
public class ApplyRight {

    private static ApplyRight instance;

    public static ApplyRight instance() {
        if (instance == null) {
            instance = new ApplyRight();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("T"), func(var("U"), var("V")));
    }

    public Closure apply(Object left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Object left;

        public Closure(Object left) {
            this.left = left;
        }

        public Object apply(Object right) {
            // TODO should be compiled as snacks code
            Method method = getMatchingAccessibleMethod(left.getClass(), "apply", new Class[] { right.getClass() });
            try {
                return method.invoke(left, right);
            } catch (ReflectiveOperationException exception) {
                throw new SnacksException(exception);
            }
        }
    }
}
