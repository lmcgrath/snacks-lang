package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class BinaryExpression extends Symbol {

    private final String operator;
    private final Symbol left;
    private final Symbol right;

    public BinaryExpression(String operator, Symbol left, Symbol right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
