package snacks.lang;

public class SnacksException extends Exception {

    public SnacksException() {
        // intentionally empty
    }

    public SnacksException(Throwable cause) {
        super(cause);
    }

    public SnacksException(String message) {
        super(message);
    }
}
