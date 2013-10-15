package snacks.lang.parser.syntax;

import java.util.Objects;

public class BreakExpression extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitBreakExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof BreakExpression;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "(Break)";
    }
}
