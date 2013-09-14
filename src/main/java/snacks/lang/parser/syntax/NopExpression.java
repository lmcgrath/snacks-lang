package snacks.lang.parser.syntax;

public class NopExpression extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitNopExpression(this);
    }
}
