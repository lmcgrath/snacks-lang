package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class MapEntry extends Symbol {

    private final Symbol key;
    private final Symbol value;

    public MapEntry(Symbol key, Symbol value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + " => " + value;
    }
}
