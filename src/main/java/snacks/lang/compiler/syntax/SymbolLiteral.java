package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class SymbolLiteral extends Symbol {

    private final String value;

    public SymbolLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ":" + value;
    }
}
