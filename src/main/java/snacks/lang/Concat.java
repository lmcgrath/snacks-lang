package snacks.lang;

public class Concat {

    private static Concat instance;

    public static Concat instance() {
        if (instance == null) {
            instance = new Concat();
        }
        return instance;
    }

    public ConcatClosure apply(Object left) {
        return new ConcatClosure(left);
    }

    public static final class ConcatClosure {

        private final Object left;

        public ConcatClosure(Object left) {
            this.left = left;
        }

        public String apply(Object right) {
            return left.toString() + right.toString();
        }
    }
}
