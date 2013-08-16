package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class IteratorLoop extends Symbol {

    private final String variable;
    private final Symbol expression;
    private final Symbol action;
    private final Symbol defaultCase;

    public IteratorLoop(String variable, Symbol expression, Symbol action, Symbol defaultCase) {
        this.variable = variable;
        this.expression = expression;
        this.action = action;
        this.defaultCase = defaultCase;
    }

    @Override
    public String toString() {
        return "(for " + variable + " in " + expression + " do " + action + (defaultCase == null ? "" : " " + defaultCase) + ")";
    }
}
