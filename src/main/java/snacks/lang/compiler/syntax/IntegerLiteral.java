package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class IntegerLiteral extends Symbol {

    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
