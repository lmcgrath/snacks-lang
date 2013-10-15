package snacks.lang.parser.syntax;

import java.util.Objects;

public class ContinueExpression extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitContinueExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ContinueExpression;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "(Continue)";
    }
}
