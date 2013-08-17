package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class SymbolLiteral extends Symbol implements Visitable {

    private final String value;

    public SymbolLiteral(String value) {
        this.value = value;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitSymbolLiteral(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof SymbolLiteral && Objects.equals(value, ((SymbolLiteral) o).value);
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
        return ":" + value;
    }
}
