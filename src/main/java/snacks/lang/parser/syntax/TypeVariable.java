package snacks.lang.parser.syntax;

import java.util.Objects;

public class TypeVariable extends VisitableSymbol {

    private final String name;

    public TypeVariable(String name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitTypeVariable(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeVariable && Objects.equals(name, ((TypeVariable) o).name);
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
        return "(TypeVariable " + name + ")";
    }
}
