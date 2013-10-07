package snacks.lang.parser.syntax;

import java.util.Objects;

public class ConstantDeclaration extends VisitableSymbol {

    private final String name;

    public ConstantDeclaration(String name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitConstantDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ConstantDeclaration && Objects.equals(name, ((ConstantDeclaration) o).name);
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
        return "(Constant " + name + ")";
    }
}
