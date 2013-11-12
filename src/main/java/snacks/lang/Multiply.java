package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.*;

@Snack(name = "*", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 13)
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
        return union(
            func(integerType(), func(integerType(), integerType())),
            func(integerType(), func(doubleType(), doubleType())),
            func(doubleType(), func(doubleType(), doubleType())),
            func(doubleType(), func(integerType(), doubleType())),
            func(stringType(), func(integerType(), stringType()))
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

