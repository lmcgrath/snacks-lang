package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class TypeSpec extends Symbol implements Visitable {

    private final Symbol type;

    public TypeSpec(Symbol type) {
        this.type = type;
    }

    @Override
    public void accept(SyntaxVisitor visitor) throws SnacksException {
        visitor.visitTypeSpec(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeSpec && Objects.equals(type, ((TypeSpec) o).type);
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
