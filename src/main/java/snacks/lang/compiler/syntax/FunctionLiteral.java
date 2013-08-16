package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

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
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FunctionLiteral) {
            FunctionLiteral other = (FunctionLiteral) o;
            return new EqualsBuilder()
                .append(arguments, other.arguments)
                .append(body, other.body)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments, body, type);
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
