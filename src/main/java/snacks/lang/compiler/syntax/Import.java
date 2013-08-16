package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Import extends Symbol {

    private final List<String> module;
    private final String alias;

    public Import(String[] module, String alias) {
        this.module = asList(module);
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Import) {
            Import other = (Import) o;
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
        if (alias.equals(module.get(module.size() - 1))) {
            return "(import " + join(module, '.') + ")";
        } else {
            return "(import " + join(module, '.') + " as " + alias + ")";
        }
    }
}
