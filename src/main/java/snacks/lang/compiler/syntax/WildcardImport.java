package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import snacks.lang.SnacksException;

public class WildcardImport extends Symbol implements Visitable {

    private final Symbol module;

    public WildcardImport(Symbol module) {
        this.module = module;
    }

    @Override
    public void accept(SyntaxVisitor visitor) throws SnacksException {
        visitor.visitWildcardImport(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof WildcardImport && Objects.equals(module, ((WildcardImport) o).module);
    }

    public Symbol getModule() {
        return module;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module);
    }

    @Override
    public String toString() {
        return "(import " + module + "._)";
    }
}
