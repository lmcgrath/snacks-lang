package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;

public interface AstNode {

    <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException;

    Type getType();
}
