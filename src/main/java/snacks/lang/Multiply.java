package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.Type.DOUBLE_TYPE;
import static snacks.lang.Type.INTEGER_TYPE;
import static snacks.lang.Type.STRING_TYPE;
import static snacks.lang.Type.func;
import static snacks.lang.Type.set;

@Snack("*")
@Infix(fixity = LEFT, precedence = 9)
public class Multiply {

    private static Multiply instance;

    public static Multiply instance() {
        if (instance == null) {
            instance = new Multiply();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return set(
            func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)),
            func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)),
            func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)),
            func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE))
        );
    }

    public DoubleClosure apply(Double left) {
        return new DoubleClosure(left);
    }

    public IntegerClosure apply(Integer left) {
        return new IntegerClosure(left);
    }

    public StringClosure apply(String left) {
        return new StringClosure(left);
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Double apply(Integer right) {
            return left * right;
        }

        public Double apply(Double right) {
            return left * right;
        }
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Integer apply(Integer right) {
            return left * right;
        }

        public Double apply(Double right) {
            return left * right;
        }
    }

    public static final class StringClosure {

        private final String left;

        public StringClosure(String left) {
            this.left = left;
        }

        public String apply(Integer right) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < right; i++) {
                builder.append(left);
            }
            return builder.toString();
        }
    }
}

