package snacks.lang.compiler.syntax;

import snacks.lang.SnacksException;

public interface Visitable {

    <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException;
}
