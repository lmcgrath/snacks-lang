package snacks.lang.parser.syntax;

import beaver.Symbol;

public class NopExpression extends Symbol implements Visitable {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitNopExpression(this);
    }
}
