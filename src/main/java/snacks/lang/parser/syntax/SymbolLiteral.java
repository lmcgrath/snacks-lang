package snacks.lang.parser.syntax;

import java.util.Objects;

public class SymbolLiteral extends VisitableSymbol {

    private final String value;

    public SymbolLiteral(String value) {
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitSymbolLiteral(this);
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
