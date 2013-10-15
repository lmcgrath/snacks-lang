package snacks.lang.parser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeReference extends VisitableSymbol {

    private final Symbol type;
    private final List<Symbol> arguments;

    public TypeReference(Symbol type, Collection<Symbol> arguments) {
        this.type = type;
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitTypeReference(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TypeReference) {
            TypeReference other = (TypeReference) o;
            return new EqualsBuilder()
                .append(type, other.type)
                .append(arguments, other.arguments)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getArguments() {
        return arguments;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, arguments);
    }

    @Override
    public String toString() {
        return "(TypeReference " + type + " " + arguments + ")";
    }
}
