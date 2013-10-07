package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.algebraic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredType extends NamedNode {

    private final String module;
    private final String name;
    private final List<NamedNode> variants;

    public DeclaredType(String module, String name, Collection<NamedNode> variants) {
        this.module = module;
        this.name = name;
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
    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        List<Type> types = new ArrayList<>();
        for (NamedNode variant : variants) {
            types.add(variant.getType());
        }
        return algebraic(module + '.' + name, types);
    }

    public List<NamedNode> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return "(Type " + module + "." + name + " " + variants + ")";
    }
}
