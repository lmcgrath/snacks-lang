package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class FunctionSignature extends VisitableSymbol {

    private final Symbol argument;
    private final Symbol result;

    public FunctionSignature(Symbol argument, Symbol result) {
        this.argument = argument;
        this.result = result;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitFunctionSignature(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FunctionSignature) {
            FunctionSignature other = (FunctionSignature) o;
            return new EqualsBuilder()
                .append(argument, other.argument)
                .append(result, other.result)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getArgument() {
        return argument;
    }

    public Symbol getResult() {
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, result);
    }

    @Override
    public String toString() {
        return "(" + argument + " -> " + result + ")";
    }
}
