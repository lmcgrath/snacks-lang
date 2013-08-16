package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class Loop extends Symbol {

    private final Symbol loopCase;
    private final Symbol defaultCase;

    public Loop(Symbol loopCase, Symbol defaultCase) {
        this.loopCase = loopCase;
        this.defaultCase = defaultCase;
    }

    @Override
    public String toString() {
        return "(" + loopCase + " " + defaultCase + ")";
    }
}
