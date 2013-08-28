package snacks.lang;

public class Plus implements Applicable {

    private static final Operations stringOps = new StringOperations();
    private static final Operations integerOps = new IntegerOperations();
    private static Plus instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Plus();
        }
        return instance;
    }

    @Override
    public Object apply(Object argument) {
        return new Closure(argument);
    }

    private static final class Closure implements Applicable {

        private final Object left;

        public Closure(Object left) {
            this.left = left;
        }

        @Override
        public Object apply(Object right) {
            return operations(left).combine(operations(right)).add(left, right);
        }
    }

    private static Operations operations(Object operand) {
        Class operandClass = operand.getClass();
        if (operandClass == Integer.class) {
            return integerOps;
        } else if (operandClass == String.class) {
            return stringOps;
        } else {
            return integerOps;
        }
    }

    private interface Operations {

        Object add(Object left, Object right);

        Operations combine(Operations other);

        Operations with(IntegerOperations other);

        Operations with(StringOperations other);
    }

    private static final class IntegerOperations implements Operations {

        @Override
        public Object add(Object left, Object right) {
            return (Integer) left + (Integer) right;
        }

        @Override
        public Operations combine(Operations other) {
            return other.with(this);
        }

        @Override
        public Operations with(IntegerOperations other) {
            return this;
        }

        @Override
        public Operations with(StringOperations other) {
            return stringOps;
        }
    }

    private static final class StringOperations implements Operations {

        @Override
        public Object add(Object left, Object right) {
            return String.valueOf(left) + right;
        }

        @Override
        public Operations combine(Operations other) {
            return other.with(this);
        }

        @Override
        public Operations with(IntegerOperations other) {
            return this;
        }

        @Override
        public Operations with(StringOperations other) {
            return this;
        }
    }
}
