package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class EnsureCase extends Symbol {

    private final Symbol expression;

    public EnsureCase(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(ensure " + expression + ")";
    }
}
