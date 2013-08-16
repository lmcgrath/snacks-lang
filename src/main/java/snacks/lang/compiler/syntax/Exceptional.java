package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.List;
import beaver.Symbol;

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
