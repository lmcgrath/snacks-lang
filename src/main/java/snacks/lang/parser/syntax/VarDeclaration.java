package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class VarDeclaration extends Symbol implements Visitable {

    private final String name;

    public VarDeclaration(String name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitVarDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof VarDeclaration && Objects.equals(name, ((VarDeclaration) o).name);
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
        return "(var " + name + ")";
    }
}
