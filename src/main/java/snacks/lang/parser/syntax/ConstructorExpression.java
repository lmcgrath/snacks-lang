package snacks.lang.parser.syntax;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ConstructorExpression extends VisitableSymbol {

    private final Symbol constructor;
    private final List<Symbol> arguments;

    public ConstructorExpression(Symbol constructor, List<Symbol> arguments) {
        this.constructor = constructor;
        this.arguments = arguments;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitConstructorExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ConstructorExpression) {
            ConstructorExpression other = (ConstructorExpression) o;
            return new EqualsBuilder()
                .append(constructor, other.constructor)
                .append(arguments, other.arguments)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getArguments() {
        return arguments;
    }

    public Symbol getConstructor() {
        return constructor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, arguments);
    }

    @Override
    public String toString() {
        return "(ConstructorExpression " + constructor + " " + arguments + ")";
    }
}
