package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.simple;

import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredConstant extends NamedNode {

    private final String qualifiedName;

    public DeclaredConstant(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredConstant(this);
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
        return simple(qualifiedName);
    }
}
