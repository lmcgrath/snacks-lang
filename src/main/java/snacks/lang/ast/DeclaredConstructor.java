package snacks.lang.ast;

import static snacks.lang.SnackKind.EXPRESSION;

import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredConstructor extends NamedNode {

    private final String module;
    private final String name;
    private final AstNode body;

    public DeclaredConstructor(String module, String name, AstNode body) {
        this.module = module;
        this.name = name;
        this.body = body;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredConstructor(this);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public SnackKind getKind() {
        return EXPRESSION;
    }

    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name + "Constructor";
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public Locator locator() {
        return new DeclarationLocator(module, name, getKind());
    }
}
