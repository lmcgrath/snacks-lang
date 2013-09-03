package snacks.lang;

public class Op$GreaterThan {

    private static Op$GreaterThan instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Op$GreaterThan();
        }
        return instance;
    }

    public Object apply(Integer left) {
        return new IntegerClosure(left);
    }

    public Object apply(Double left) {
        return new DoubleClosure(left);
    }

    public static final class IntegerClosure {

        private final Integer left;

        public IntegerClosure(Integer left) {
            this.left = left;
        }

        public Object apply(Integer right) {
            return left > right;
        }

        public Object apply(Double right) {
            return left > right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Object apply(Integer right) {
            return left > right;
        }

        public Object apply(Double right) {
            return left > right;
        }
    }
}
