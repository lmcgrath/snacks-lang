package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class Argument extends Symbol {

    private final String name;
    private final Symbol type;

    public Argument(String name, Symbol type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == null) {
            return name;
        } else {
            return name + ":" + type;
        }
    }
}
