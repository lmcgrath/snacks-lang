package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ExceptionalExpression extends Symbol implements Visitable {

    private final List<Symbol> useCases;
    private final Symbol expression;
    private final List<Symbol> embraceCases;
    private final Symbol ensureCase;

    public ExceptionalExpression(Symbol[] useCases, Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        this.expression = expression;
        this.ensureCase = ensureCase;
        this.useCases = useCases == null ? new ArrayList<Symbol>() : asList(useCases);
        this.embraceCases = embraceCases == null ? new ArrayList<Symbol>() : asList(embraceCases);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitExceptional(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ExceptionalExpression) {
            ExceptionalExpression other = (ExceptionalExpression) o;
            return new EqualsBuilder()
                .append(useCases, other.useCases)
                .append(expression, other.expression)
                .append(embraceCases, other.embraceCases)
                .append(ensureCase, other.ensureCase)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getEmbraceCases() {
        return embraceCases;
    }

    public Symbol getEnsureCase() {
        return ensureCase;
    }

    public Symbol getExpression() {
        return expression;
    }

    public List<Symbol> getUseCases() {
        return useCases;
    }

    @Override
    public int hashCode() {
        return Objects.hash(useCases, expression, embraceCases, ensureCase);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (useCases.isEmpty()) {
            builder.append("(begin ");
        } else {
            builder.append("(");
            builder.append(join(useCases, ", "));
            builder.append(" do ");
        }
        builder.append(expression);
        if (!embraceCases.isEmpty()) {
            builder.append(" ");
            builder.append(join(embraceCases, " "));
        }
        if (ensureCase != null) {
            builder.append(" ");
            builder.append(ensureCase);
        }
        builder.append(")");
        return builder.toString();
    }
}
