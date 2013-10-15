package snacks.lang.parser.syntax;

import java.util.Objects;

public class AnyMatcher extends VisitableSymbol {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAnyMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof AnyMatcher;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "(AnyMatcher)";
    }
}
