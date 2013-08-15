package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class DefaultCase extends Symbol {

    private final Symbol expression;

    public DefaultCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(else " + expression + ")";
    }
}
