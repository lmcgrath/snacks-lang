package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public interface AstNode {

    <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException;

    Reference getReference();

    Type getType();

    boolean hasType();

    boolean isFunction();

    boolean isReference();
}
