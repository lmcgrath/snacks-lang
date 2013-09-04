package snacks.lang;

public class Op$Plus {

    private static Op$Plus instance;

    public static Op$Plus instance() {
        if (instance == null) {
            instance = new Op$Plus();
        }
        return instance;
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
