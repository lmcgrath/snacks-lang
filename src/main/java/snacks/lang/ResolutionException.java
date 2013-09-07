package snacks.lang;

public class ResolutionException extends RuntimeException {

    public ResolutionException(String message) {
        super(message);
    }

    public ResolutionException(Throwable cause) {
        super(cause);
    }
}
