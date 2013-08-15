package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class BooleanLiteral extends Symbol {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value ? "True" : "False";
    }
}
