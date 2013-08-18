package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class Identifier extends Symbol implements Visitable {

    private final String value;

    public Identifier(String value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitIdentifier(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Identifier && Objects.equals(value, ((Identifier) o).value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}