package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class FunctionLiteral extends Symbol {

    private final List<Argument> arguments;
    private final Symbol body;
    private final TypeSpec type;

    public FunctionLiteral(Argument[] arguments, Symbol body, TypeSpec type) {
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
