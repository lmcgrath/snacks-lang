package snacks.lang.parser.syntax;

import beaver.Symbol;

public class BreakExpression extends Symbol implements Visitable {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.BreakExpression(this);
    }
}
