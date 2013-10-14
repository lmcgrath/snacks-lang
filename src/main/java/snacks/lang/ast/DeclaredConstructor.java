package snacks.lang.ast;

import static snacks.lang.SnackKind.EXPRESSION;

import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredConstructor extends NamedNode {

    private final String qualifiedName;
    private final AstNode body;

    public DeclaredConstructor(String qualifiedName, AstNode body) {
        this.qualifiedName = qualifiedName;
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
    public String getQualifiedName() {
        return qualifiedName + "Constructor";
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public Locator locator() {
        return new DeclarationLocator(qualifiedName, getKind());
    }

    @Override
    public String toString() {
        return "(Constructor " + qualifiedName + " " + body + ")";
    }
}
