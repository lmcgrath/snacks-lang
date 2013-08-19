package snacks.lang.compiler;

import snacks.lang.SnacksException;

public class UndefinedSymbolException extends SnacksException {

    public UndefinedSymbolException(String message) {
        super(message);
    }
}
