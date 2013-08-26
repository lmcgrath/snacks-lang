package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class FunctionLiteral extends Symbol implements Visitable {

    private final Symbol argument;
    private final Symbol body;
    private final Symbol type;

    public FunctionLiteral(Symbol argument, Symbol body, Symbol type) {
        this.argument = argument;
        this.body = body;
        this.type = type;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitFunctionLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FunctionLiteral) {
            FunctionLiteral other = (FunctionLiteral) o;
            return new EqualsBuilder()
                .append(argument, other.argument)
                .append(body, other.body)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getArgument() {
        return argument;
    }

    public Symbol getBody() {
        return body;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, body, type);
    }

    @Override
    public String toString() {
        if (type == null) {
            return "(" + argument + " -> " + body + ")";
        } else {
            return "(" + argument + " :: " + type + " -> " + body + ")";
        }
    }
}
