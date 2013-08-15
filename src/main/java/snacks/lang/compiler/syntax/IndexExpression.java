package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class IndexExpression extends Symbol {

    private final Symbol expression;
    private final List<Symbol> arguments;

    public IndexExpression(Symbol expression, Symbol... arguments) {
        this.expression = expression;
        this.arguments = asList(arguments);
    }

    @Override
    public String toString() {
        return expression + "[" + join(arguments, ", ") + "]";
    }
}
