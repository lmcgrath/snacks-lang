package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class SubImport extends Symbol implements Visitable {

    private final String module;
    private final String alias;

    public SubImport(String module, String alias) {
        this.module = module;
        this.alias = alias;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitSubImport(this, state);
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

    public String getAlias() {
        return alias;
    }

    public String getModule() {
        return module;
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
