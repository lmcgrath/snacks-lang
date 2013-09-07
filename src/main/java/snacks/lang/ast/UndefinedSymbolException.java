package snacks.lang.ast;

public class UndefinedSymbolException extends RuntimeException {

    public UndefinedSymbolException(String message) {
        super(message);
    }

    public UndefinedSymbolException(String message, Throwable cause) {
        super(message, cause);
    }
}
