package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;
import iddic.lang.compiler.lexer.Position;

public interface SyntaxNode {

    <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException;

    Position getEnd();

    Position getStart();
}
