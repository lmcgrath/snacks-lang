package snacks.lang.compiler.ast;

public class UndefinedSymbolException extends RuntimeException {

    public UndefinedSymbolException(String message) {
        super(message);
    }
}
