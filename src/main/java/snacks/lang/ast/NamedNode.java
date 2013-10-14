package snacks.lang.ast;

import static snacks.lang.JavaUtils.javaClass;

import snacks.lang.SnackKind;

public abstract class NamedNode extends AstNode {

    public String getJavaClass() {
        return javaClass(getModule(), getSimpleName()).replace('.', '/');
    }

    public abstract SnackKind getKind();

    public String getModule() {
        return getQualifiedName().substring(0, getQualifiedName().lastIndexOf('.'));
    }

    public String getSimpleName() {
        return getQualifiedName().substring(getQualifiedName().lastIndexOf('.') + 1);
    }

    public Locator locator() {
        return new DeclarationLocator(getQualifiedName(), getKind());
    }

    public abstract String getQualifiedName();
}
