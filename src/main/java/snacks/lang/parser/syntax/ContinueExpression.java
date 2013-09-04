package snacks.lang.parser.syntax;

import beaver.Symbol;

public class ContinueExpression extends Symbol implements Visitable {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitContinueExpression(this);
    }
}
