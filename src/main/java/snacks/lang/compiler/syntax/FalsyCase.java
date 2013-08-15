package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class FalsyCase extends Symbol {

    private final Symbol condition;
    private final Symbol expression;

    public FalsyCase(Symbol condition, Symbol expression) {
        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(unless " + condition + " then " + expression + ")";
    }
}
