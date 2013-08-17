package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class NothingLiteral extends Symbol implements Visitable {

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitNothingLiteral(this, state);
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
