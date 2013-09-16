package snacks.lang.compiler;

public class CompileException extends RuntimeException {

    public CompileException(String message) {
        super(message);
    }

    public CompileException(Exception cause) {
        super(cause);
    }
}
