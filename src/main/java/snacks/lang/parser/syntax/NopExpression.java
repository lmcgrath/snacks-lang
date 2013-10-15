package snacks.lang.parser.syntax;

import java.util.Objects;

public class NopExpression extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitNopExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof NopExpression;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "(Nop)";
    }
}
