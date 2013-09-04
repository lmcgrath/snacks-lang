package snacks.lang;

import java.util.Objects;

public class Op$Equals {

    private static Op$Equals instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Op$Equals();
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
            return Objects.equals(left, right);
        }
    }
}
