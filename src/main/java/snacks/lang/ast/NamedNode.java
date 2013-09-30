package snacks.lang.ast;

import snacks.lang.SnackKind;

public abstract class NamedNode extends AstNode {

    public abstract SnackKind getKind();

    public Locator locator() {
        return new DeclarationLocator(getModule(), getName(), getKind());
    }

    public abstract String getModule();

    public abstract String getName();
}
