package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class UnaryExpression extends Symbol {

    private final String operator;
    private final Symbol operand;

    public UnaryExpression(String operator, Symbol operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "(" + operator + " " + operand + ")";
    }
}
