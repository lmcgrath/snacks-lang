package snacks.lang.parser.syntax;

import java.util.Objects;

public class TypeSpec extends VisitableSymbol {

    private final QualifiedIdentifier name;

    public TypeSpec(QualifiedIdentifier name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitTypeSpec(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeSpec && Objects.equals(name, ((TypeSpec) o).name);
    }

    public QualifiedIdentifier getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
