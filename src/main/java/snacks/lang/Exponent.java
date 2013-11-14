package snacks.lang;

import static java.lang.Math.pow;
import static snacks.lang.Fixity.RIGHT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.doubleType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.integerType;
import static snacks.lang.Types.union;

@Snack(name = "**", kind = EXPRESSION)
@Infix(fixity = RIGHT, precedence = 14)
public class Exponent {

    private static Exponent instance;

    public static Exponent instance() {
        if (instance == null) {
            instance = new Exponent();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(
            func(integerType(), func(integerType(), doubleType())),
            func(integerType(), func(doubleType(), doubleType())),
            func(doubleType(), func(integerType(), doubleType())),
            func(doubleType(), func(doubleType(), doubleType()))
        );
    }

    public IntegerClosure apply(Integer left) {
        return new IntegerClosure(left);
    }

    public DoubleClosure apply(Double left) {
        return new DoubleClosure(left);
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Double apply(Integer right) {
            return pow(left, right);
        }

        public Double apply(Double right) {
            return pow(left, right);
        }
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Double apply(Integer right) {
            return pow(left, right);
        }

        public Double apply(Double right) {
            return pow(left, right);
        }
    }
}
