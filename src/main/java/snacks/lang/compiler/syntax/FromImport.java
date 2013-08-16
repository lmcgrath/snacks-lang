package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class FromImport extends Symbol {

    private final List<String> module;
    private final List<Symbol> subImports;

    public FromImport(String[] module, Symbol... subImports) {
        this.module = asList(module);
        this.subImports = asList(subImports);
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

    @Override
    public int hashCode() {
        return Objects.hash(module, subImports);
    }

    @Override
    public String toString() {
        return "(from " + join(module, '.') + " import " + join(subImports, ", ") + ")";
    }
}
