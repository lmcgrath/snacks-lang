package snacks.lang;

public class Concat {

    private static Concat instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Concat();
        }
        return instance;
    }

    public Object apply(Object left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Object left;

        public Closure(Object left) {
            this.left = left;
        }

        public Object apply(Object right) {
            return left.toString() + right.toString();
        }
    }
}
