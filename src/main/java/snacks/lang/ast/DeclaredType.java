package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.algebraic;

import java.util.ArrayList;
import java.util.List;
import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredType extends NamedNode {

    private final String module;
    private final String name;
    private final List<AstNode> variants;

    public DeclaredType(String module, String name) {
        this.module = module;
        this.name = name;
        this.variants = new ArrayList<>();
    }

    public void addVariant(AstNode variant) {
        variants.add(variant);
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
        return algebraic(module + '.' + name);
    }

    public List<AstNode> getVariants() {
        return variants;
    }
}
