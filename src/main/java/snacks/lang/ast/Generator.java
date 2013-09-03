package snacks.lang.ast;

public interface Generator {

    void generateApply(Apply node);

    void generateAssign(Assign node);

    void generateBegin(Begin begin);

    void generateBooleanConstant(BooleanConstant node);

    void generateClosure(Closure node);

    void generateClosureLocator(ClosureLocator locator);

    void generateDeclarationLocator(DeclarationLocator locator);

    void generateDeclaredArgument(DeclaredArgument node);

    void generateDeclaredExpression(DeclaredExpression node);

    void generateDoubleConstant(DoubleConstant node);

    void generateEmbrace(Embrace node);

    void generateExceptional(Exceptional node);

    void generateExpressionConstant(ExpressionConstant node);

    void generateFunction(Function node);

    void generateGuardCase(GuardCase node);

    void generateGuardCases(GuardCases node);

    void generateIntegerConstant(IntegerConstant node);

    void generateReference(Reference node);

    void generateReferencesEqual(ReferencesEqual node);

    void generateResult(Result node);

    void generateSequence(Sequence node);

    void generateStringConstant(StringConstant node);

    void generateSymbol(SymbolConstant node);

    void generateVariableDeclaration(VariableDeclaration node);

    void generateVariableLocator(VariableLocator locator);

    void generateVoidApply(VoidApply node);

    void generateVoidFunction(VoidFunction node);

    void visitCharacterConstant(CharacterConstant node);
}
