package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class DoubleLiteral extends Symbol {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
