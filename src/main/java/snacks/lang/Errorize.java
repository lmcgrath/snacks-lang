package snacks.lang;

public class Errorize {

    private static Errorize instance;

    public static Errorize instance() {
        if (instance == null) {
            instance = new Errorize();
        }
        return instance;
    }

    public SnacksException apply(String message) {
        return new SnacksException(message);
    }

    public SnacksException apply(SnacksException exception) {
        return exception;
    }
}
