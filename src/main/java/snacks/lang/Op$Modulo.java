package snacks.lang;

public class Op$Modulo {

    private static Op$Modulo instance;

    public static Op$Modulo instance() {
        if (instance == null) {
            instance = new Op$Modulo();
        }
        return instance;
    }

    public Closure apply(Integer left) {
        return new Closure(left);
    }

    public static final class Closure {

        private final Integer left;

        public Closure(Integer left) {
            this.left = left;
        }

        public Integer apply(Integer right) {
            return left % right;
        }
    }
}
