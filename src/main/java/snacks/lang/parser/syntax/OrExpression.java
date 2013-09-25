package snacks.lang.parser.syntax;

import beaver.Symbol;

public class OrExpression extends VisitableSymbol {

    private final Symbol left;
    private final Symbol right;

    public OrExpression(Symbol left, Symbol right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitOrExpression(this);
    }

    public Symbol getLeft() {
        return left;
    }

    public Symbol getRight() {
        return right;
    }
}
