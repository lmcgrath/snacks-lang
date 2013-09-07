package snacks.lang;

import static snacks.lang.Type.DOUBLE_TYPE;
import static snacks.lang.Type.INTEGER_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.set;

@Snack("-")
public class Minus {

    private static Minus instance;

    public static Minus instance() {
        if (instance == null) {
            instance = new Minus();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return set(
            func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)),
            func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE))
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
            return left - right;
        }

        public Double apply(Double right) {
            return left - right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Double apply(Integer right) {
            return left - right;
        }

        public Double apply(Double right) {
            return left - right;
        }
    }
}
