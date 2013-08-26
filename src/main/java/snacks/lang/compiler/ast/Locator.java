package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;

public interface Locator {

    void accept(AstVisitor visitor) throws SnacksException;
}
