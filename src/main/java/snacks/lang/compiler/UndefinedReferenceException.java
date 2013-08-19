package snacks.lang.compiler;

import snacks.lang.SnacksException;

public class UndefinedReferenceException extends SnacksException {

    public UndefinedReferenceException(String message) {
        super(message);
    }
}
