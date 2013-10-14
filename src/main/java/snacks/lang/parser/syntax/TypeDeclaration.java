package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeDeclaration extends VisitableSymbol {

    private final String name;
    private final List<String> parameters;
    private final List<Symbol> variants;

    public TypeDeclaration(String name, Collection<String> parameters, Collection<Symbol> variants) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
        this.variants = new ArrayList<>(variants);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitTypeDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TypeDeclaration) {
            TypeDeclaration other = (TypeDeclaration) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(parameters, other.parameters)
                .append(variants, other.variants)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Symbol> getVariants() {
        return variants;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters, variants);
    }

    @Override
    public String toString() {
        if (parameters.isEmpty()) {
            return "(Type " + name + " " + variants + ")";
        } else {
            return "(Type " + name + "<" + join(parameters, ", ") + ">" + variants + ")";
        }
    }
}
