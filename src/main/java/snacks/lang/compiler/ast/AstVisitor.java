package snacks.lang.compiler.ast;

import snacks.lang.SnacksException;

public interface AstVisitor {

    void visitApply(Apply node) throws SnacksException;

    void visitArgument(Variable node) throws SnacksException;

    void visitBooleanConstant(BooleanConstant node) throws SnacksException;

    void visitDeclarationLocator(DeclarationLocator locator) throws SnacksException;

    void visitDeclaredExpression(DeclaredExpression node) throws SnacksException;

    void visitDoubleConstant(DoubleConstant node) throws SnacksException;

    void visitFunction(Function node) throws SnacksException;

    void visitIntegerConstant(IntegerConstant node) throws SnacksException;

    void visitInstantiable(Instantiable node) throws SnacksException;

    void visitInstantiate(Instantiate instantiate) throws SnacksException;

    void visitReference(Reference node) throws SnacksException;

    void visitResult(Result node) throws SnacksException;

    void visitSequence(Sequence node) throws SnacksException;

    void visitStringConstant(StringConstant node) throws SnacksException;

    void visitVariableDeclaration(VariableDeclaration node) throws SnacksException;

    void visitVariableLocator(VariableLocator locator) throws SnacksException;
}
