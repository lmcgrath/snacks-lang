package snacks.lang.compiler.syntax;

import snacks.lang.SnacksException;

public interface SyntaxVisitor<R, S> {

    R visitAccessExpression(AccessExpression node, S state) throws SnacksException;

    R visitAnnotated(Annotated node, S state) throws SnacksException;

    R visitAnnotation(Annotation node, S state) throws SnacksException;

    R visitApplyExpression(ApplyExpression node, S state) throws SnacksException;

    R visitArgument(Argument node, S state) throws SnacksException;

    R visitBinaryExpression(BinaryExpression node, S state) throws SnacksException;

    R visitBlock(Block node, S state) throws SnacksException;

    R visitBooleanLiteral(BooleanLiteral node, S state) throws SnacksException;

    R visitCharacterLiteral(CharacterLiteral node, S state) throws SnacksException;

    R visitConditional(Conditional node, S state) throws SnacksException;

    R visitDeclaration(Declaration node, S state) throws SnacksException;

    R visitDefaultCase(DefaultCase node, S state) throws SnacksException;

    R visitDoubleLiteral(DoubleLiteral node, S state) throws SnacksException;

    R visitEmbraceCase(EmbraceCase node, S state) throws SnacksException;

    R visitEnsureCase(EnsureCase node, S state) throws SnacksException;

    R visitExceptional(Exceptional node, S state) throws SnacksException;

    R visitFalsyCase(FalsyCase node, S state) throws SnacksException;

    R visitFromImport(FromImport node, S state) throws SnacksException;

    R visitFunctionLiteral(FunctionLiteral node, S state) throws SnacksException;

    R visitHurl(Hurl node, S state) throws SnacksException;

    R visitIdentifier(Identifier node, S state) throws SnacksException;

    R visitImport(Import node, S state) throws SnacksException;

    R visitIndexExpression(IndexExpression node, S state) throws SnacksException;

    R visitIntegerLiteral(IntegerLiteral node, S state) throws SnacksException;

    R visitInvokableLiteral(InvokableLiteral node, S state) throws SnacksException;

    R visitInvokeExpression(InvokeExpression node, S state) throws SnacksException;

    R visitIteratorLoop(IteratorLoop node, S state) throws SnacksException;

    R visitListLiteral(ListLiteral node, S state) throws SnacksException;

    R visitLoop(Loop node, S state) throws SnacksException;

    R visitMapEntry(MapEntry node, S state) throws SnacksException;

    R visitMapLiteral(MapLiteral node, S state) throws SnacksException;

    R visitModule(Module node, S state) throws SnacksException;

    R visitNothingLiteral(NothingLiteral node, S state) throws SnacksException;

    R visitQualifiedIdentifier(QualifiedIdentifier node, S state) throws SnacksException;

    R visitRegexLiteral(RegexLiteral node, S state) throws SnacksException;

    R visitResult(Result node, S state) throws SnacksException;

    R visitSetLiteral(SetLiteral node, S state) throws SnacksException;

    R visitStringInterpolation(StringInterpolation node, S state) throws SnacksException;

    R visitStringLiteral(StringLiteral node, S state) throws SnacksException;

    R visitSubImport(SubImport node, S state) throws SnacksException;

    R visitSymbolLiteral(SymbolLiteral node, S state) throws SnacksException;

    R visitTruthyCase(TruthyCase node, S state) throws SnacksException;

    R visitTupleLiteral(TupleLiteral node, S state) throws SnacksException;

    R visitTypeSpec(TypeSpec node, S state) throws SnacksException;

    R visitUnaryExpression(UnaryExpression node, S state) throws SnacksException;

    R visitUsing(Using node, S state) throws SnacksException;

    R visitVar(Var node, S state) throws SnacksException;

    R visitWildcardImport(WildcardImport node, S state) throws SnacksException;
}
