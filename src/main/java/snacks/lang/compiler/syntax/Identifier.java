package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class Identifier extends Symbol {

    private final String value;

    public Identifier(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
