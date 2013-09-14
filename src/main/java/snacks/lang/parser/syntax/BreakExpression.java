package snacks.lang.parser.syntax;

public class BreakExpression extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitBreakExpression(this);
    }
}
