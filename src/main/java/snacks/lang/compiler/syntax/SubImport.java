package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class SubImport extends Symbol {

    private final String module;
    private final String alias;

    public SubImport(String module, String alias) {
        this.module = module;
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof SubImport) {
            SubImport other = (SubImport) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(alias, other.alias)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, alias);
    }

    @Override
    public String toString() {
        if (alias.equals(module)) {
            return module;
        } else {
            return module + " as " + alias;
        }
    }
}
