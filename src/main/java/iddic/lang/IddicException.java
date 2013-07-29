package iddic.lang;

public class IddicException extends Exception {

    private static final long serialVersionUID = 2218186377308285267L;

    public IddicException() {
        // intentionally empty
    }

    public IddicException(String message) {
        super(message);
    }

    public IddicException(Throwable cause) {
        super(cause);
    }

    public IddicException(String message, Throwable cause) {
        super(message, cause);
    }
}
