package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.algebraic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public List<NamedNode> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return "(Type " + qualifiedName + " " + variants + ")";
    }
}
