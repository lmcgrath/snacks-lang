package iddic.lang.compiler.syntax;

import iddic.lang.IddicException;

public interface SyntaxVisitor<R, S> {

    R visitAccess(Access access, S state) throws IddicException;

    R visitAccessAssign(AccessAssign assign, S state) throws IddicException;

    R visitArgumentsList(ArgumentsList list, S state) throws IddicException;

    R visitBinaryExpression(BinaryExpression binary, S state) throws IddicException;

    R visitBlockLiteral(BlockExpression block, S state) throws IddicException;

    R visitBooleanLiteral(BooleanLiteral literal, S state) throws IddicException;

    R visitCharacterLiteral(CharacterLiteral literal, S state) throws IddicException;

    R visitCompoundImportExpression(CompoundImportExpression imports, S state) throws IddicException;

    R visitConditional(Conditional conditional, S state) throws IddicException;

    R visitDeclaration(Declaration declaration, S state) throws IddicException;

    R visitMapLiteral(MapLiteral literal, S state) throws IddicException;

    R visitDoubleLiteral(DoubleLiteral literal, S state) throws IddicException;

    R visitEmbrace(Embrace embrace, S state) throws IddicException;

    R visitFalsyCondition(FalsyCondition condition, S state) throws IddicException;

    R visitFunctionArguments(FunctionArguments list, S state) throws IddicException;

    R visitFunctionLiteral(FunctionLiteral function, S state) throws IddicException;

    R visitGlueExpression(GlueExpression glue, S state) throws IddicException;

    R visitIdentifier(Identifier identifier, S state) throws IddicException;

    R visitIdentifierAlias(IdentifierAlias alias, S state) throws IddicException;

    R visitImportExpression(ImportExpression imports, S state) throws IddicException;

    R visitIndexAccess(IndexAccess access, S state) throws IddicException;

    R visitIndexAssign(IndexAssign assign, S state) throws IddicException;

    R visitIntegerLiteral(IntegerLiteral literal, S state) throws IddicException;

    R visitInterpolation(Interpolation interpolation, S state) throws IddicException;

    R visitListLiteral(ListLiteral literal, S state) throws IddicException;

    R visitMetaAnnotation(MetaAnnotation meta, S state) throws IddicException;

    R visitModuleDeclaration(ModuleDeclaration module, S state) throws IddicException;

    R visitNothingLiteral(NothingLiteral literal, S state) throws IddicException;

    R visitQualifiedIdentifier(QualifiedIdentifier identifier, S state) throws IddicException;

    R visitRootIdentifier(RootIdentifier root, S state) throws IddicException;

    R visitSelector(Selector selector, S state) throws IddicException;

    R visitSetLiteral(SetLiteral literal, S state) throws IddicException;

    R visitStringLiteral(StringLiteral literal, S state) throws IddicException;

    R visitSymbolLiteral(SymbolLiteral literal, S state) throws IddicException;

    R visitTruthyCondition(TruthyCondition condition, S state) throws IddicException;

    R visitTupleLiteral(TupleLiteral literal, S state) throws IddicException;

    R visitUnaryExpression(UnaryExpression unary, S state) throws IddicException;

    R visitXRangeLiteral(XRangeLiteral xrange, S state) throws IddicException;
}
