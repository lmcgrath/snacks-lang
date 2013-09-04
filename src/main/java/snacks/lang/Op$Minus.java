package snacks.lang;

public class Op$Minus {

    private static Op$Minus instance;

    public static Op$Minus instance() {
        if (instance == null) {
            instance = new Op$Minus();
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

        public Integer apply(Integer right) {
            return left - right;
        }

        public Double apply(Double right) {
            return left - right;
        }
    }

    public static final class DoubleClosure {

        private final Double left;

        public DoubleClosure(Double left) {
            this.left = left;
        }

        public Double apply(Integer right) {
            return left - right;
        }

        public Double apply(Double right) {
            return left - right;
        }
    }
}
