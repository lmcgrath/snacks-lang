package snacks.lang;

public class Op$GreaterThanEquals {

    private static Op$GreaterThanEquals instance;

    public static Op$GreaterThanEquals instance() {
        if (instance == null) {
            instance = new Op$GreaterThanEquals();
        }
        return instance;
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
            return left >= right;
        }

        public Boolean apply(Double right) {
            return left >= right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Boolean apply(Integer right) {
            return left >= right;
        }

        public Boolean apply(Double right) {
            return left >= right;
        }
    }
}
