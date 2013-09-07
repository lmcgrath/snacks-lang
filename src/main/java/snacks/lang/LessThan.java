package snacks.lang;

import static snacks.lang.Type.*;

@Snack("<")
public class LessThan {

    private static LessThan instance;

    public static LessThan instance() {
        if (instance == null) {
            instance = new LessThan();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return set(
            func(INTEGER_TYPE, func(INTEGER_TYPE, BOOLEAN_TYPE)),
            func(INTEGER_TYPE, func(DOUBLE_TYPE, BOOLEAN_TYPE)),
            func(DOUBLE_TYPE, func(INTEGER_TYPE, BOOLEAN_TYPE)),
            func(DOUBLE_TYPE, func(DOUBLE_TYPE, BOOLEAN_TYPE))
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
            return left < right;
        }

        public Boolean apply(Double right) {
            return left < right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Boolean apply(Integer right) {
            return left < right;
        }

        public Boolean apply(Double right) {
            return left < right;
        }
    }
}
