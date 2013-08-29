package snacks.lang;

public class Multiply {

    private static Multiply instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Multiply();
        }
        return instance;
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

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Object apply(Integer right) {
            return left * right;
        }

        public Object apply(Double right) {
            return left * right;
        }
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Object apply(Integer right) {
            return left * right;
        }

        public Object apply(Double right) {
            return left * right;
        }
    }

    public static final class StringClosure {

        private final String left;

        public StringClosure(String left) {
            this.left = left;
        }

        public Object apply(Integer right) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < right; i++) {
                builder.append(left);
            }
            return builder.toString();
        }
    }
}

