package snacks.lang;

import java.util.Objects;

public class Op$Equals {

    private static Op$Equals instance;

    public static Op$Equals instance() {
        if (instance == null) {
            instance = new Op$Equals();
        }
        return instance;
    }

    public EqualsClosure apply(Object left) {
        return new EqualsClosure(left);
    }

    public static final class EqualsClosure {

        private final Object left;

        public EqualsClosure(Object left) {
            this.left = left;
        }

        public Boolean apply(Object right) {
            return Objects.equals(left, right);
        }
    }
}
