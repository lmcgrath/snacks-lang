package snacks.lang;

public class Errorize {

    private static Errorize instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Errorize();
        }
        return instance;
    }

    public Object apply(String message) {
        return new SnacksException(message);
    }

    public Object apply(SnacksException exception) {
        return exception;
    }
}
