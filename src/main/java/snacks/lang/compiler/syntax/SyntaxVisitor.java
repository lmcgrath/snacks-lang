package snacks.lang.compiler.syntax;

public interface SyntaxVisitor {

    void visitAccessExpression(AccessExpression node);

    void visitAnnotated(Annotated node);

    void visitAnnotation(Annotation node);

    void visitApplyExpression(ApplyExpression node);

    void visitArgument(Argument node);

    void visitBlock(Block node);

    void visitBooleanLiteral(BooleanLiteral node);

    void visitCharacterLiteral(CharacterLiteral node);

    void visitConditional(Conditional node);

    void visitDeclaration(Declaration node);

    void visitDefaultCase(DefaultCase node);

    void visitDoubleLiteral(DoubleLiteral node);

    void visitEmbraceCase(EmbraceCase node);

    void visitEnsureCase(EnsureCase node);

    void visitExceptional(Exceptional node);

    void visitFalsyCase(FalsyCase node);

    void visitFromImport(FromImport node);

    void visitFunctionLiteral(FunctionLiteral node);

    void visitHurl(Hurl node);

    void visitIdentifier(Identifier node);

    void visitImport(Import node);

    void visitIndexExpression(IndexExpression node);

    void visitIntegerLiteral(IntegerLiteral node);

    void visitInstantiableLiteral(InstantiableLiteral node);

    void visitInstantiationExpression(InstantiationExpression node);

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

    void visitStringInterpolation(StringInterpolation node);

    void visitStringLiteral(StringLiteral node);

    void visitSubImport(SubImport node);

    void visitSymbolLiteral(SymbolLiteral node);

    void visitTruthyCase(TruthyCase node);

    void visitTupleLiteral(TupleLiteral node);

    void visitTypeSpec(TypeSpec node);

    void visitUsing(Using node);

    void visitVar(Var node);

    void visitWildcardImport(WildcardImport node);
}
