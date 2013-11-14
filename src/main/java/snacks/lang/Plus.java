package snacks.lang;

import static snacks.lang.Fixity.LEFT;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.*;

@Snack(name = "+", kind = EXPRESSION)
@Infix(fixity = LEFT, precedence = 12)
public class Plus {

    private static Plus instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Plus();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return union(
            func(booleanType(), func(stringType(), stringType())),
            func(integerType(), func(integerType(), integerType())),
            func(integerType(), func(doubleType(), doubleType())),
            func(integerType(), func(stringType(), stringType())),
            func(doubleType(), func(doubleType(), doubleType())),
            func(doubleType(), func(integerType(), doubleType())),
            func(doubleType(), func(stringType(), stringType())),
            func(stringType(), func(stringType(), stringType())),
            func(stringType(), func(integerType(), stringType())),
            func(stringType(), func(doubleType(), stringType())),
            func(stringType(), func(booleanType(), stringType()))
        );
    }

    public BooleanClosure apply(Boolean left) {
        return new BooleanClosure(left);
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

    public static final class BooleanClosure {

        private final Boolean left;

        public BooleanClosure(Boolean left) {
            this.left = left;
        }

        public String apply(String right) {
            return left + right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Double apply(Double right) {
            return left + right;
        }

        public Double apply(Integer right) {
            return left + right;
        }

        public String apply(String right) {
            return left + right;
        }
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Double apply(Double right) {
            return left + right;
        }

        public Integer apply(Integer right) {
            return left + right;
        }

        public String apply(String right) {
            return left + right;
        }
    }

    public static final class StringClosure {

        private final String left;

        public StringClosure(String left) {
            this.left = left;
        }

        public String apply(Boolean right) {
            return left + right;
        }

        public String apply(Double right) {
            return left + right;
        }

        public String apply(Integer right) {
            return left + right;
        }

        public String apply(String right) {
            return left + right;
        }
    }
}
