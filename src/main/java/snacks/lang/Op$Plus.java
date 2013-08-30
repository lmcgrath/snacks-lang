package snacks.lang;

public class Op$Plus {

    private static Op$Plus instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Op$Plus();
        }
        return instance;
    }

    public Object apply(Boolean left) {
        return new BooleanClosure(left);
    }

    public Object apply(Double left) {
        return new DoubleClosure(left);
    }

    public Object apply(Integer left) {
        return new IntegerClosure(left);
    }

    public Object apply(String left) {
        return new StringClosure(left);
    }

    public static final class BooleanClosure {

        private final Boolean left;

        public BooleanClosure(Boolean left) {
            this.left = left;
        }

        public Object apply(String right) {
            return left + right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Object apply(Double right) {
            return left + right;
        }

        public Object apply(Integer right) {
            return left + right;
        }

        public Object apply(String right) {
            return left + right;
        }
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Object apply(Double right) {
            return left + right;
        }

        public Object apply(Integer right) {
            return left + right;
        }

        public Object apply(String right) {
            return left + right;
        }
    }

    public static final class StringClosure {

        private final String left;

        public StringClosure(String left) {
            this.left = left;
        }

        public Object apply(Boolean right) {
            return left + right;
        }

        public Object apply(Double right) {
            return left + right;
        }

        public Object apply(Integer right) {
            return left + right;
        }

        public Object apply(String right) {
            return left + right;
        }
    }
}
