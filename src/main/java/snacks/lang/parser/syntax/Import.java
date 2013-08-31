package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Import extends Symbol implements Visitable {

    private final Symbol module;
    private final String alias;

    public Import(Symbol module, String alias) {
        this.module = module;
        this.alias = alias;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitImport(this);
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

    public String getAlias() {
        return alias;
    }

    public Symbol getModule() {
        return module;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, alias);
    }

    @Override
    public String toString() {
        if (alias.equals(((QualifiedIdentifier) module).getLastSegment())) {
            return "(import " + module + ")";
        } else {
            return "(import " + module + " as " + alias + ")";
        }
    }
}
