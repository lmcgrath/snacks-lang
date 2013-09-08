package snacks.lang;

public class SnacksException extends RuntimeException {

    public SnacksException(String message) {
        super(message);
    }

    public SnacksException(Throwable cause) {
        super(cause);
    }
}
