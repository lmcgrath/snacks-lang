package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Exceptional extends Symbol {

    private final List<Symbol> usings;
    private final Symbol expression;
    private final List<Symbol> embraceCases;
    private final Symbol ensureCase;

    public Exceptional(Symbol[] usings, Symbol expression, Symbol[] embraceCases, Symbol ensureCase) {
        this.expression = expression;
        this.ensureCase = ensureCase;
        this.usings = usings == null ? new ArrayList<Symbol>() : asList(usings);
        this.embraceCases = embraceCases == null ? new ArrayList<Symbol>() : asList(embraceCases);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Exceptional) {
            Exceptional other = (Exceptional) o;
            return new EqualsBuilder()
                .append(usings, other.usings)
                .append(expression, other.expression)
                .append(embraceCases, other.embraceCases)
                .append(ensureCase, other.ensureCase)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(usings, expression, embraceCases, ensureCase);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (usings.isEmpty()) {
            builder.append("(begin ");
        } else {
            builder.append("(");
            builder.append(join(usings, ", "));
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
