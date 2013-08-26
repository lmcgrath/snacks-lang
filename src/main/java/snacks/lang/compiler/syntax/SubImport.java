package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class SubImport extends Symbol implements Visitable {

    private final String expression;
    private final String alias;

    public SubImport(String expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public void accept(SyntaxVisitor visitor) throws SnacksException {
        visitor.visitSubImport(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof SubImport) {
            SubImport other = (SubImport) o;
            return new EqualsBuilder()
                .append(expression, other.expression)
                .append(alias, other.alias)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getAlias() {
        return alias;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, alias);
    }

    @Override
    public String toString() {
        if (alias.equals(expression)) {
            return expression;
        } else {
            return expression + " as " + alias;
        }
    }
}
