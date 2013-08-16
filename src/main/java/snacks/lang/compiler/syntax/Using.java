package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class Using extends Symbol {

    private final String name;
    private final Symbol expression;

    public Using(String name, Symbol expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(using " + (name == null ? expression : name + " = " + expression) + ")";
    }
}
