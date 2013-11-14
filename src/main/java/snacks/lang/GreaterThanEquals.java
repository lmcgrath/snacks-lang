package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.*;

@Snack(name = ">=", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 6)
public class GreaterThanEquals {

    private static GreaterThanEquals instance;

    public static Object instance() {
        if (instance == null) {
            instance = new GreaterThanEquals();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(
            func(integerType(), func(integerType(), booleanType())),
            func(integerType(), func(doubleType(), booleanType())),
            func(doubleType(), func(integerType(), booleanType())),
            func(doubleType(), func(doubleType(), booleanType()))
        );
    }

    public IntegerClosure apply(Integer left) {
        return new IntegerClosure(left);
    }

    public DoubleClosure apply(Double left) {
        return new DoubleClosure(left);
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Boolean apply(Integer right) {
            return left >= right;
        }

        public Boolean apply(Double right) {
            return left >= right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Boolean apply(Integer right) {
            return left >= right;
        }

        public Boolean apply(Double right) {
            return left >= right;
        }
    }
}
