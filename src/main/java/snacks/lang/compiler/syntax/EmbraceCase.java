package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class EmbraceCase extends Symbol {

    private final String argument;
    private final Symbol type;
    private final Symbol expression;

    public EmbraceCase(String argument, Symbol type, Symbol expression) {
        this.argument = argument;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(embrace " + argument + (type == null ? "" : ":" + type) + " -> " + expression + ")";
    }
}
