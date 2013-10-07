package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.simple;

import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredConstant extends NamedNode {

    private final String module;
    private final String name;

    public DeclaredConstant(String module, String name) {
        this.module = module;
        this.name = name;
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
    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return simple(module + '.' + name);
    }
}
