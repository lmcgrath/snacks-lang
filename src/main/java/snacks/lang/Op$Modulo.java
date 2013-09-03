package snacks.lang;

public class Op$Modulo {

    private static Op$Modulo instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Op$Modulo();
        }
        return instance;
    }

    public Object apply(Integer left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Integer left;

        public Closure(Integer left) {
            this.left = left;
        }

        public Object apply(Integer right) {
            return left % right;
        }
    }
}
