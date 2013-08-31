package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class NothingLiteral extends Symbol implements Visitable {

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitNothingLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof NothingLiteral;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "Nothing";
    }
}
