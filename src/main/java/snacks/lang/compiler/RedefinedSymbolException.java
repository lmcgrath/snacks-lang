package snacks.lang.compiler;

import snacks.lang.SnacksException;

public class RedefinedSymbolException extends SnacksException {

    public RedefinedSymbolException(String message) {
        super(message);
    }
}
