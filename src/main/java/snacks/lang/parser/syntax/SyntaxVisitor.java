package snacks.lang.parser.syntax;

public interface SyntaxVisitor {

    void visitAccessExpression(AccessExpression node);

    void visitApplyExpression(ApplyExpression node);

    void visitArgument(Argument node);

    void visitAssignmentExpression(AssignmentExpression node);

    void visitBlock(Block node);

    void visitBooleanLiteral(BooleanLiteral node);

    void visitBreakExpression(BreakExpression node);

    void visitCharacterLiteral(CharacterLiteral node);

    void visitConditionCase(ConditionCase node);

    void visitConditional(Conditional node);

    void visitContinueExpression(ContinueExpression node);

    void visitDeclaration(Declaration node);

    void visitDoubleLiteral(DoubleLiteral node);

    void visitEmbraceCase(EmbraceCase node);

    void visitExceptional(ExceptionalExpression node);

    void visitFromImport(FromImport node);

    void visitFunctionLiteral(FunctionLiteral node);

    void visitFunctionSignature(FunctionSignature node);

    void visitHurl(HurlExpression node);

    void visitIdentifier(Identifier node);

    void visitImport(Import node);

    void visitInitializerExpression(InitializerExpression node);

    void visitIntegerLiteral(IntegerLiteral node);

    void visitInvocation(Invocation node);

    void visitInvokableLiteral(InvokableLiteral node);

    void visitIteratorLoop(IteratorLoop node);

    void visitListLiteral(ListLiteral node);

    void visitLoopExpression(LoopExpression node);

    void visitMapEntry(MapEntry node);

    void visitMapLiteral(MapLiteral node);

    void visitMessage(Message node);

    void visitModule(Module node);

    void visitNopExpression(NopExpression node);

    void visitOperatorDeclaration(OperatorDeclaration node);

    void visitPropertyDeclaration(PropertyDeclaration node);

    void visitPropertyExpression(PropertyExpression node);

    void visitQualifiedIdentifier(QualifiedIdentifier node);

    void visitQuotedIdentifier(QuotedIdentifier node);

    void visitRecordDeclaration(RecordDeclaration node);

    void visitRegexLiteral(RegexLiteral node);

    void visitResult(Result node);

    void visitSetLiteral(SetLiteral node);

    void visitSignature(Signature node);

    void visitStringLiteral(StringLiteral node);

    void visitSubImport(SubImport node);

    void visitSymbolLiteral(SymbolLiteral node);

    void visitTupleLiteral(TupleLiteral node);

    void visitTupleSignature(TupleSignature node);

    void visitTypeDeclaration(TypeDeclaration node);

    void visitTypeSpec(TypeSpec node);

    void visitTypeVariable(TypeVariable node);

    void visitUsing(Using node);

    void visitVar(Var node);

    void visitVarDeclaration(VarDeclaration node);

    void visitWildcardImport(WildcardImport node);
}
