package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public interface AstNode {

    void accept(AstVisitor visitor) throws SnacksException;

    Type getType();
}
