package snacks.lang.parser.syntax;

public class UnitLiteral extends VisitableSymbol {

    public static final UnitLiteral INSTANCE = new UnitLiteral();

    private UnitLiteral() {
        // intentionally empty
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitUnitLiteral(this);
    }

    @Override
    public String toString() {
        return "()";
    }
}
