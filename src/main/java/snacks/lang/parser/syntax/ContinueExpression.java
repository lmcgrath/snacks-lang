package snacks.lang.parser.syntax;

public class ContinueExpression extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitContinueExpression(this);
    }
}
