package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeDeclaration extends VisitableSymbol {

    private final String name;
    private final List<Symbol> variants;

    public TypeDeclaration(String name, Symbol... variants) {
        this.name = name;
        this.variants = asList(variants);
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
                .append(variants, other.variants)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getVariants() {
        return variants;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, variants);
    }

    @Override
    public String toString() {
        return "(Type " + name + " " + variants + ")";
    }
}
