package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class BooleanLiteral extends Symbol implements Visitable {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitBooleanLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof BooleanLiteral && value == ((BooleanLiteral) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value ? "True" : "False";
    }
}
