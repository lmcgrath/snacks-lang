package snacks.lang;

public class Multiply implements Applicable {

    private static Multiply instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Multiply();
        }
        return instance;
    }

    @Override
    public Object apply(Object argument) {
        return new Closure((Integer) argument);
    }

    private class Closure implements Applicable {

        private final int value;

        public Closure(int value) {
            this.value = value;
        }

        @Override
        public Object apply(Object argument) {
            return value * (Integer) argument;
        }
    }
}

