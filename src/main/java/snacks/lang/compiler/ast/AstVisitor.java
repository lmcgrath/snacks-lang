package snacks.lang.compiler.ast;

public interface AstVisitor {

    void visitApply(Apply node);

    void visitBooleanConstant(BooleanConstant node);

    void visitClosure(Closure node);

    void visitClosureLocator(ClosureLocator locator);

    void visitDeclarationLocator(ExpressionLocator locator);

    void visitDeclaredArgument(DeclaredArgument node);

    void visitDeclaredExpression(DeclaredExpression node);

    void visitDoubleConstant(DoubleConstant node);

    void visitExpressionConstant(ExpressionConstant node);

    void visitFunction(Function node);

    void visitIntegerConstant(IntegerConstant node);

    void visitReference(Reference node);

    void visitResult(Result node);

    void visitSequence(Sequence node);

    void visitStringConstant(StringConstant node);

    void visitVariableDeclaration(VariableDeclaration node);

    void visitVariableLocator(VariableLocator locator);

    void visitVoidApply(VoidApply node);

    void visitVoidFunction(VoidFunction node);
}
