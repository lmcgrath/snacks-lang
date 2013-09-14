package snacks.lang.parser.syntax;

import java.util.Objects;

public class Identifier extends VisitableSymbol {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitIdentifier(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Identifier && Objects.equals(name, ((Identifier) o).name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
