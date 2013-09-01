package snacks.lang.parser.syntax;

public interface SyntaxVisitor {

    void visitApplyExpression(ApplyExpression node);

    void visitArgument(Argument node);

    void visitAssignmentExpression(AssignmentExpression node);

    void visitBlock(Block node);

    void visitBooleanLiteral(BooleanLiteral node);

    void visitCharacterLiteral(CharacterLiteral node);

    void visitConditional(Conditional node);

    void visitDeclaration(Declaration node);

    void visitDoubleLiteral(DoubleLiteral node);

    void visitEmbraceCase(EmbraceCase node);

    void visitEnsureCase(EnsureCase node);

    void visitExceptional(Exceptional node);

    void visitFromImport(FromImport node);

    void visitFunctionLiteral(FunctionLiteral node);

    void visitHurl(Hurl node);

    void visitIdentifier(Identifier node);

    void visitImport(Import node);

    void visitIntegerLiteral(IntegerLiteral node);

    void visitInvokableLiteral(InvokableLiteral node);

    void visitInvocation(Invocation node);

    void visitIsExpression(IsExpression node);

    void visitIteratorLoop(IteratorLoop node);

    void visitListLiteral(ListLiteral node);

    void visitLoop(Loop node);

    void visitMapEntry(MapEntry node);

    void visitMapLiteral(MapLiteral node);

    void visitModule(Module node);

    void visitNothingLiteral(NothingLiteral node);

    void visitQualifiedIdentifier(QualifiedIdentifier node);

    void visitRegexLiteral(RegexLiteral node);

    void visitResult(Result node);

    void visitSetLiteral(SetLiteral node);

    void visitStringLiteral(StringLiteral node);

    void visitSubImport(SubImport node);

    void visitSymbolLiteral(SymbolLiteral node);

    void visitConditionCase(ConditionCase node);

    void visitTupleLiteral(TupleLiteral node);

    void visitTypeSpec(TypeSpec node);

    void visitUsing(Using node);

    void visitVar(Var node);

    void visitVarDeclaration(VarDeclaration node);

    void visitWildcardImport(WildcardImport node);
}