package snacks.lang.parser.syntax;

import java.util.Objects;

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
    public boolean equals(Object o) {
        return o == this || o instanceof UnitLiteral;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "()";
    }
}
