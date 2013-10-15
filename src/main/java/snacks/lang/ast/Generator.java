package snacks.lang.ast;

public interface Generator {

    void generate(AstNode node);

    void generateAccess(Access node);

    void generateApply(Apply node);

    void generateAssign(Assign node);

    void generateBegin(Begin node);

    void generateBooleanConstant(BooleanConstant node);

    void generateBreak(Break node);

    void generateCharacterConstant(CharacterConstant node);

    void generateClosure(Closure node);

    void generateClosureLocator(ClosureLocator locator);

    void generateContinue(Continue node);

    void generateDeclarationLocator(DeclarationLocator locator);

    void generateDeclaredConstant(DeclaredConstant node);

    void generateDeclaredConstructor(DeclaredConstructor node);

    void generateDeclaredExpression(DeclaredExpression node);

    void generateDeclaredRecord(DeclaredRecord node);

    void generateDeclaredType(DeclaredType node);

    void generateDoubleConstant(DoubleConstant node);

    void generateEmbrace(Embrace node);

    void generateExceptional(Exceptional node);

    void generateExpressionConstant(ExpressionConstant node);

    void generateFunction(Function node);

    void generateFunctionClosure(FunctionClosure node);

    void generateGuardCase(GuardCase node);

    void generateGuardCases(GuardCases node);

    void generateHurl(Hurl node);

    void generateInitializer(Initializer node);

    void generateIntegerConstant(IntegerConstant node);

    void generateLogicalAnd(LogicalAnd node);

    void generateLogicalOr(LogicalOr node);

    void generateLoop(Loop node);

    void generateMatchConstant(MatchConstant node);

    void generateMatchConstructor(MatchConstructor node);

    void generateNop(Nop node);

    void generatePatternCase(PatternCase node);

    void generatePatternCases(PatternCases node);

    void generateReference(Reference node);

    void generateReferencesEqual(ReferencesEqual node);

    void generateResult(Result node);

    void generateSequence(Sequence node);

    void generateStringConstant(StringConstant node);

    void generateSymbol(SymbolConstant node);

    void generateTupleInitializer(TupleInitializer node);

    void generateUnitConstant(UnitConstant node);

    void generateVariableDeclaration(VariableDeclaration node);

    void generateVariableLocator(VariableLocator locator);

    void generateVoidFunction(VoidFunction node);
}
