package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class FunctionLiteral extends Symbol {

    private final List<Symbol> arguments;
    private final Symbol body;
    private final Symbol type;

    public FunctionLiteral(Symbol[] arguments, Symbol body, Symbol type) {
        this.arguments = asList(arguments);
        this.body = body;
        this.type = type;
    }

    @Override
    public String toString() {
        String head = join(arguments, ", ");
        if (type == null) {
            return "(" + head + " -> " + body + ")";
        } else {
            return "(" + head + " $ " + type + " -> " + body + ")";
        }
    }
}
