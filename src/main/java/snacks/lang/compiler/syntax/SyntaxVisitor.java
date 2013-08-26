package snacks.lang.compiler.syntax;

import snacks.lang.SnacksException;

public interface SyntaxVisitor {

    void visitAccessExpression(AccessExpression node) throws SnacksException;

    void visitAnnotated(Annotated node) throws SnacksException;

    void visitAnnotation(Annotation node) throws SnacksException;

    void visitApplyExpression(ApplyExpression node) throws SnacksException;

    void visitArgument(Argument node) throws SnacksException;

    void visitBinaryExpression(BinaryExpression node) throws SnacksException;

    void visitBlock(Block node) throws SnacksException;

    void visitBooleanLiteral(BooleanLiteral node) throws SnacksException;

    void visitCharacterLiteral(CharacterLiteral node) throws SnacksException;

    void visitConditional(Conditional node) throws SnacksException;

    void visitDeclaration(Declaration node) throws SnacksException;

    void visitDefaultCase(DefaultCase node) throws SnacksException;

    void visitDoubleLiteral(DoubleLiteral node) throws SnacksException;

    void visitEmbraceCase(EmbraceCase node) throws SnacksException;

    void visitEnsureCase(EnsureCase node) throws SnacksException;

    void visitExceptional(Exceptional node) throws SnacksException;

    void visitFalsyCase(FalsyCase node) throws SnacksException;

    void visitFromImport(FromImport node) throws SnacksException;

    void visitFunctionLiteral(FunctionLiteral node) throws SnacksException;

    void visitHurl(Hurl node) throws SnacksException;

    void visitIdentifier(Identifier node) throws SnacksException;

    void visitImport(Import node) throws SnacksException;

    void visitIndexExpression(IndexExpression node) throws SnacksException;

    void visitIntegerLiteral(IntegerLiteral node) throws SnacksException;

    void visitInstantiableLiteral(InstantiableLiteral node) throws SnacksException;

    void visitInstantiationExpression(InstantiationExpression node) throws SnacksException;

    void visitIteratorLoop(IteratorLoop node) throws SnacksException;

    void visitListLiteral(ListLiteral node) throws SnacksException;

    void visitLoop(Loop node) throws SnacksException;

    void visitMapEntry(MapEntry node) throws SnacksException;

    void visitMapLiteral(MapLiteral node) throws SnacksException;

    void visitModule(Module node) throws SnacksException;

    void visitNothingLiteral(NothingLiteral node) throws SnacksException;

    void visitQualifiedIdentifier(QualifiedIdentifier node) throws SnacksException;

    void visitRegexLiteral(RegexLiteral node) throws SnacksException;

    void visitResult(Result node) throws SnacksException;

    void visitSetLiteral(SetLiteral node) throws SnacksException;

    void visitStringInterpolation(StringInterpolation node) throws SnacksException;

    void visitStringLiteral(StringLiteral node) throws SnacksException;

    void visitSubImport(SubImport node) throws SnacksException;

    void visitSymbolLiteral(SymbolLiteral node) throws SnacksException;

    void visitTruthyCase(TruthyCase node) throws SnacksException;

    void visitTupleLiteral(TupleLiteral node) throws SnacksException;

    void visitTypeSpec(TypeSpec node) throws SnacksException;

    void visitUsing(Using node) throws SnacksException;

    void visitVar(Var node) throws SnacksException;

    void visitWildcardImport(WildcardImport node) throws SnacksException;
}
