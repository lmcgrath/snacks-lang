package snacks.lang.parser.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeReference extends VisitableSymbol {

    private final Symbol type;
    private final List<Symbol> parameters;

    public TypeReference(Symbol type, Collection<Symbol> parameters) {
        this.type = type;
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TypeReference) {
            TypeReference other = (TypeReference) o;
            return new EqualsBuilder()
                .append(type, other.type)
                .append(parameters, other.parameters)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getParameters() {
        return parameters;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, parameters);
    }

    @Override
    public String toString() {
        return "(TypeReference " + type + " " + parameters + ")";
    }
}
