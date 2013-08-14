package iddic.lang.compiler.lexer;

public class ScanException extends RuntimeException {

    public ScanException(String message) {
        super(message);
    }

    public ScanException(Throwable cause) {
        super(cause);
    }
}
