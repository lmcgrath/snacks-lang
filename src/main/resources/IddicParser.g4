parser grammar IddicParser;

options { tokenVocab=IddicLexer; }

@header {
package iddic.lang.compiler;
}

moduleDeclaration
    : moduleExpression (term+ moduleExpression)* term*
    | term*
    ;

moduleExpression
    : declaration
    | importExpression
    ;

importExpression
    : Import qualifiedId importAlias?                   # Import
    | Import qualifiedId Multiply                       # WildcardImport
    | From qualifiedId Import importList                # FromImport
    | From qualifiedId Import LParen importList? RParen # ParentheticalFromImport
    | From qualifiedId Import Multiply                  # WildcardFromImport
    ;

importList
    : subImport (Comma subImport)* Comma?
    ;

subImport
    : Id importAlias?
    ;

importAlias
    : As Id
    ;

declaration
    : metaAnnotations? Id typeSpec? Assign expression
    ;

metaAnnotations
    : metaAnnotation (NewLine* metaAnnotation)* NewLine*
    ;

metaAnnotation
    : At qualifiedId selector?
    ;

expression
    : selector selector*
    | expression op=Exponent<assoc=right> expression
    | op=(Plus | Minus | BitNot) expression
    | expression op=(Multiply | Divide | Modulo) expression
    | expression op=(Plus | Minus) expression
    | expression op=BitAnd expression
    | expression op=BitOr expression
    | expression op=BitXor expression
    | expression op=Coalesce expression
    | expression op=(ShiftLeft | ShiftRight | UShiftRight) expression
    | expression op=(In
        | NotIn
        | Equals
        | NotEquals
        | LessThan
        | LessThanEquals
        | GreaterThan
        | GreaterThanEquals
        | Is
        | IsNot) expression
    | op=LogicalNot expression
    | expression op=LogicalAnd expression
    | expression op=LogicalOr expression
    | expression op=Apply<assoc=right> expression
    | expression op=(Range | XRange) expression
    | expression op=Assign<assoc=right> expression
    ;

selector
    : primaryExpression suffix*
    ;

suffix
    : Dot Id # Accessor
    | Indexer expressionList? RSquare # Indexer
    ;

primaryExpression
    : Id                                     # Identifier
    | Symbol                                 # SymbolLiteral
    | LCurly (entryList | Colon) RCurly      # ExpressionMapLiteral
    | LCurly symbolList RCurly               # SymbolMapLiteral
    | LParen expression RParen               # ParentheticalExpression
    | LCurly sequenceExpressions? RCurly     # BlockExpression
    | LSquare expressionList? RSquare        # ListLiteral
    | LParen expressionList? RParen          # TupleLiteral
    | LCurly (expressionList | Comma) RCurly # SetLiteral
    | regex                                  # RegexLiteral
    | stringExpression                       # StringLiteral
    | Character                              # CharacterLiteral
    | Integer                                # IntegerLiteral
    | Double                                 # DoubleLiteral
    | value=(True | False)                   # BooleanLiteral
    | Nothing                                # NothingLiteral
    | function                               # FunctionLiteral
    | embraceExpression                      # Embrace
    | conditionalExpression                  # Conditional
    | loopExpression                         # Loop
    ;

regex
    : LRegex interpolation* RRegex RegexOptions
    ;

stringExpression
    : LDQuote interpolation* RDQuote   # InterpolatedString
    | LQuote String? RQuote            # String
    | LNowDoc NowDoc? LNowDoc          # NowDoc
    | LHereDoc interpolation* RHereDoc # HereDoc
    ;

interpolation
    : LInterpolate expression? RInterpolate # ExpressionInterpolation
    | String                                # StringInterpolation
    ;

embraceExpression
    : Begin usingClause* sequenceExpressions embraceClause* ensureClause? End
    ;

usingClause
    : Using Id Assign expression term+
    ;

embraceClause
    : Embrace Id multiTypeSpec? AppliesTo sequenceExpressions
    ;

multiTypeSpec
    : (Colon | KeyColon) qualifiedId (BitOr qualifiedId)*
    ;

ensureClause
    : Ensure sequenceExpressions
    ;

conditionalExpression
    : If expression Then sequenceExpressions alternativeConditional* elseConditional? End
    | Unless expression Then sequenceExpressions alternativeConditional* elseConditional? End
    ;

alternativeConditional
    : ElseIf expression Then sequenceExpressions
    | ElseUnless expression Then sequenceExpressions
    ;

elseConditional
    : Else sequenceExpressions
    ;

loopExpression
    : While expression Then sequenceExpressions End
    | Until expression Then sequenceExpressions End
    ;

forExpression
    : For forElements Then sequenceExpressions End
    ;

forElements
    : Id In expression
    | Id GoesTo Id In expression
    ;

function
    : LParen argument* RParen typeSpec? AppliesTo expression                  # TerminalFunction
    | LParen argument* functionTypeSpec? AppliesTo expression RParen          # CanonicalFunction
    | LCurly argument* functionTypeSpec? AppliesTo sequenceExpressions RCurly # BlockFunction
    ;

argument
    : Id typeSpec? # FunctionArgument
    | Throwaway    # ThrowawayArgument
    ;

functionTypeSpec
    : Apply qualifiedId RParen
    ;

typeSpec
    : (Colon | KeyColon) qualifiedId
    ;

sequenceExpressions
    : statement (term+ statement)* term*
    ;

statement
    : Hurl expression             # HurlStatement
    | Return expression           # ReturnStatement
    | Var Id (Assign expression)? # VarDeclarationStatement
    | expression                  # ExpressionStatement
    ;

expressionList
    : expression NewLine* (Comma expression)* Comma?
    ;

symbolList
    : symbolEntry (Comma symbolEntry)* Comma?
    ;

symbolEntry
    : KeySymbol expression NewLine*   # KeySymbolEntry
    | Id KeyColon expression NewLine* # IdSymbolEntry
    ;

entryList
    : entry (Comma entry)* Comma?
    ;

entry
    : expression GoesTo expression
    ;

qualifiedId
    : Id (Dot Id)*
    ;

term
    : NewLine
    | Semicolon
    ;
