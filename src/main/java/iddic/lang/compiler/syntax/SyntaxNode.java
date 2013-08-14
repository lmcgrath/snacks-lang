package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;

public interface SyntaxNode {

    <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException;
}
