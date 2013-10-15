package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.algebraic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredType extends NamedNode {

    private final String qualifiedName;
    private final List<NamedNode> variants;

    public DeclaredType(String qualifiedName, Collection<NamedNode> variants) {
        this.qualifiedName = qualifiedName;
        this.variants = new ArrayList<>(variants);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredType) {
            DeclaredType other = (DeclaredType) o;
            return new EqualsBuilder()
                .append(qualifiedName, other.qualifiedName)
                .append(variants, other.variants)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredType(this);
    }

    @Override
    public SnackKind getKind() {
        return TYPE;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public Type getType() {
        List<Type> types = new ArrayList<>();
        for (NamedNode variant : variants) {
            types.add(variant.getType());
        }
        return algebraic(qualifiedName, types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, variants);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredType(this);
    }

    public List<NamedNode> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return "(Type " + qualifiedName + " " + variants + ")";
    }
}
