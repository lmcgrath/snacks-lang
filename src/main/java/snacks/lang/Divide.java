package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.doubleType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.integerType;
import static snacks.lang.Types.union;

@Snack(name = "/", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 13)
public class Divide {

    private static Divide instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Divide();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(
            func(integerType(), func(integerType(), integerType())),
            func(integerType(), func(doubleType(), doubleType())),
            func(doubleType(), func(doubleType(), doubleType())),
            func(doubleType(), func(integerType(), doubleType()))
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

        public Integer apply(Integer right) {
            return left / right;
        }

        public Double apply(Double right) {
            return left / right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Double apply(Integer right) {
            return left / right;
        }

        public Double apply(Double right) {
            return left / right;
        }
    }
}
