package snacks.lang.parser.syntax;

import beaver.Symbol;

public class NillableExpression extends Symbol implements Visitable {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitNillableExpression(this);
    }
}
