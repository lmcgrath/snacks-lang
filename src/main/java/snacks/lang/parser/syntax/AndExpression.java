package snacks.lang.parser.syntax;

import beaver.Symbol;

public class AndExpression extends VisitableSymbol {

    private final Symbol left;
    private final Symbol right;

    public AndExpression(Symbol left, Symbol right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAndExpression(this);
    }

    public Symbol getLeft() {
        return left;
    }

    public Symbol getRight() {
        return right;
    }
}
