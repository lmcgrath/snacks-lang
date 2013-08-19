package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;

public interface AstVisitor<R, S> {

    R visitApply(Apply node, S state) throws SnacksException;

    R visitBooleanConstant(BooleanConstant node, S state) throws SnacksException;

    R visitDeclaredExpression(DeclaredExpression node, S state) throws SnacksException;

    R visitDoubleConstant(DoubleConstant node, S state) throws SnacksException;

    R visitIntegerConstant(IntegerConstant node, S state) throws SnacksException;

    R visitReference(Reference node, S state) throws SnacksException;

    R visitStringConstant(StringConstant node, S state) throws SnacksException;
}
