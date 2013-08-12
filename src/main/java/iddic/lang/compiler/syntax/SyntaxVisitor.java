package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;

public interface SyntaxVisitor<R, S> {

    R visitApplyNode(ApplyNode node, S state) throws IddicException;

    R visitBooleanNode(BooleanNode node, S state) throws IddicException;

    R visitDoubleNode(DoubleNode node, S state) throws IddicException;

    R visitIdentifierNode(IdentifierNode node, S state) throws IddicException;

    R visitIntegerNode(IntegerNode node, S state) throws IddicException;

    R visitNothingNode(NothingNode node, S state) throws IddicException;

    R visitStringNode(StringNode node, S state) throws IddicException;
}
