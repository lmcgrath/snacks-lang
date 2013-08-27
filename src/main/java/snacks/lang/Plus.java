package snacks.lang;

public class Plus implements Applicable {

    private static Plus instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Plus();
        }
        return instance;
    }

    @Override
    public Object apply(Object argument) {
        return new closure((Integer) argument);
    }

    private class closure implements Applicable {

        private final int value;

        public closure(int value) {
            this.value = value;
        }

        @Override
        public Object apply(Object argument) {
            return value + (Integer) argument;
        }
    }
}
