package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class TruthyCase extends Symbol {

    private final Symbol condition;
    private final Symbol expression;

    public TruthyCase(Symbol condition, Symbol expression) {
        this.condition = condition;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(if " + condition + " then " + expression + ")";
    }
}
