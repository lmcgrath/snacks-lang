package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class FromImport extends Symbol implements Visitable {

    private final Symbol module;
    private final List<Symbol> subImports;

    public FromImport(Symbol module, Symbol... subImports) {
        this.module = module;
        this.subImports = asList(subImports);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitFromImport(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FromImport) {
            FromImport other = (FromImport) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(subImports, other.subImports)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getModule() {
        return module;
    }

    public List<Symbol> getSubImports() {
        return subImports;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, subImports);
    }

    @Override
    public String toString() {
        return "(from " + module + " import " + join(subImports, ", ") + ")";
    }
}
