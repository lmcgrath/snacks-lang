lexer grammar IddicLexer;

tokens {
    String, LInterpolate, RInterpolate, NowDoc
}

@header {
package iddic.lang.compiler;

import static java.lang.Character.*;
import java.util.ArrayDeque;
import java.util.Deque;
}

@members {

private final Deque<Integer> braces = new ArrayDeque<>();

@Override
public void reset() {
    super.reset();
    braces.clear();
}

private void beginInterpolation() {
    setType(LInterpolate);
    pushMode(DEFAULT_MODE);
    braces.push(0);
}

private void bracesDown() {
    if (!braces.isEmpty()) {
        braces.push(braces.pop() - 1);
    }
}

private void bracesUp() {
    if (braces.isEmpty()) {
        braces.push(1);
    } else {
        braces.push(braces.pop() + 1);
    }
}

private boolean detectIndexer() {
    if (inputAt(1) == '[') {
        pushMode(INDEXER_MODE);
        return true;
    } else {
        return false;
    }
}

private boolean detectRInterpolate() {
    if (!braces.isEmpty() && braces.peek() < 0) {
        setType(RInterpolate);
        popMode();
        braces.pop();
        return true;
    } else {
        return false;
    }
}

private void eatNewLines() {
    try (LookAheadContext context = new LookAheadContext(_input)) {
        int i = 0;
        while (true) {
            char peek = inputAt(++i);
            if (isWhitespace(peek)) {
                if (peek == '\r' || peek == '\n') {
                    pushMode(EAT_NEWLINE_MODE);
                    break;
                }
            } else {
                break;
            }
        }
    }
}

private char inputAt(int offset) {
    return (char) _input.LA(offset);
}
}

fragment StringCharacter:   ~['"\\\r\n] | EscapeSequence;
fragment EscapeSequence:    UnicodeEscape | OctalEscape | AsciiEscape;
fragment UnicodeEscape:     '\\' [uU] Hex Hex Hex;
fragment OctalEscape:       '\\' [0-3]? Octal Octal;
fragment AsciiEscape:       '\\' [btnfr\"\'\\];
fragment Digit:             [0-9];
fragment Hex:               [a-fA-F0-9];
fragment Octal:             [0-7];
fragment NotInterpolation:  ('#' ~'{') | '\\#{';

fragment StringInterpolation
    : ~['"\\\r\n#]+
    | EscapeSequence+
    | NotInterpolation+
    ;

fragment RegexCharacter
    : ~[\\/]+
    | '\\/'+
    | EscapeSequence+
    ;

fragment IdHead
    : [a-zA-Z_]
    | ~[\u0000-\u00FF\uD800-\uDBFF] { isJavaIdentifierStart(inputAt(-1)) }?
    | [\uD800-\uDBFF] [\uDC00-\uDFFF] { isJavaIdentifierStart(toCodePoint(inputAt(-2), inputAt(-1))) }?
    ;

fragment IdTail
    : [a-zA-Z0-9$_]
    | ~[\u0000-\u00FF\uD800-\uDBFF] { isJavaIdentifierPart(inputAt(-1)) }?
    |  [\uD800-\uDBFF] [\uDC00-\uDFFF] { isJavaIdentifierPart(toCodePoint(inputAt(-2), inputAt(-1))) }?
    ;

fragment Sym
    : Id '='
    | '[]='
    | '[]'
    | '.='
    | Equals
    | In
    | LessThan
    | GreaterThan
    | LessThanEquals
    | GreaterThanEquals
    | ShiftLeft
    | ShiftRight
    | UShiftRight
    | Range
    | XRange
    | Plus
    | Minus
    | Multiply
    | Divide
    | Modulo
    | BitAnd
    | BitOr
    | BitXor
    | BitNot
    | Coalesce
    | Exponent
    ;

LRegex:            'r/' -> pushMode(REGEX_INTERPOLATION_MODE);
KeySymbol:          Sym ':' { eatNewLines(); };
Symbol:             ':' Sym { detectIndexer(); };
IdSymbol:           ':' Id { setType(Symbol); detectIndexer(); };
Comment:            '/*' .*? '*/' -> skip;
LineComment:        '//' ~[\r\n]* -> skip;
NotEquals:          ('<>' | '!=') { eatNewLines(); };
True:               'True' { detectIndexer(); };
False:              'False' { detectIndexer(); };
Nothing:            'Nothing' { detectIndexer(); };
LogicalAnd:         ('and' | '&&') { eatNewLines(); };
As:                 'as';
Begin:              'begin' { eatNewLines(); };
ElseUnless:         'else unless';
ElseIf:             'else if';
Else:               'else' { eatNewLines(); };
If:                 'if';
Unless:             'unless';
Embrace:            'embrace' { eatNewLines(); };
End:                'end';
Ensure:             'ensure' { eatNewLines(); };
For:                'for';
From:               'from';
Hurl:               'hurl';
Import:             'import';
LogicalNot:         ('not' | '!') { eatNewLines(); };
LogicalOr:          ('or' | '||') { eatNewLines(); };
Return:             'return';
Then:               'then' { eatNewLines(); };
Until:              'until';
Using:              'using';
Var:                'var';
While:              'while';
Semicolon:          ';' { eatNewLines(); };
Colon:              ':' { eatNewLines(); };
BitNot:             '~' { eatNewLines(); };
IsNot:              'is not' { eatNewLines(); };
Is:                 'is' { eatNewLines(); };
Equals:             '==' { eatNewLines(); };
GoesTo:             '=>' { eatNewLines(); };
Assign:             '=' { eatNewLines(); };
LessThanEquals:     '<=' { eatNewLines(); };
ShiftLeft:          '<<' { eatNewLines(); };
LessThan:           '<' { eatNewLines(); };
GreaterThanEquals:  '>=' { eatNewLines(); };
ShiftRight:         '>>' { eatNewLines(); };
UShiftRight:        '>>>' { eatNewLines(); };
GreaterThan:        '>' { eatNewLines(); };
In:                 'in' { eatNewLines(); };
NotIn:              'not in' { eatNewLines(); };
AppliesTo:          '->' { eatNewLines(); };
XRange:             '...' { eatNewLines(); };
Range:              '..' { eatNewLines(); };
Dot:                '.' { eatNewLines(); };
Exponent:           '**' { eatNewLines(); };
Multiply:           '*' { eatNewLines(); };
Divide:             '/' { eatNewLines(); };
Modulo:             '%' { eatNewLines(); };
Plus:               '+' { eatNewLines(); };
Minus:              '-' { eatNewLines(); };
BitAnd:             '&' { eatNewLines(); };
BitOr:              '|' { eatNewLines(); };
BitXor:             '^' { eatNewLines(); };
Coalesce:           '?' { eatNewLines(); };
Apply:              '$' { eatNewLines(); };
LParen:             '(' { eatNewLines(); };
RParen:             ')' { detectIndexer(); };
LSquare:            '[' { eatNewLines(); };
RSquare:            ']' { detectIndexer(); };
LCurly:             '{' { bracesUp(); eatNewLines(); };
RCurly:             '}' { bracesDown(); if (!detectRInterpolate()) { detectIndexer(); } };
Comma:              ',' { eatNewLines(); };
At:                 '@';
LHereDoc:           '"""' '\n'? -> pushMode(HEREDOC_MODE);
LNowDoc:            '\'\'\'' '\n'? -> pushMode(NOWDOC_MODE);
LDQuote:            '"' -> pushMode(STRING_INTERPOLATION_MODE);
LQuote:             '\'' -> pushMode(STRING_MODE);
Double:             Digit* '.' Digit+ { detectIndexer(); };
Integer:            ('0' | [1-9] Digit*) { detectIndexer(); };
Character:          [Cc] '\'' StringCharacter '\'' { detectIndexer(); };
Throwaway:          '_' { !isJavaIdentifierPart(inputAt(1)) }?;
IdKey:              Id { inputAt(1) == ':' }? { setType(Id); pushMode(COLON_MODE); eatNewLines(); };
Id:                 IdHead IdTail* [\?!]? { detectIndexer(); };
IgnoreNewLine:      '\\' [\r\n] -> skip;
WhiteSpace:         [ \t\u000C]+ -> skip;
NewLine:            [\r\n]+;

mode LIST_MODE;

ListWhitespace:     [ \r\n\t\u000C]+ -> skip;
IndexSymbol:        '[]:' { setType(KeySymbol); popMode(); eatNewLines(); };
IndexAssignSymbol:  '[]=:' { setType(KeySymbol); popMode(); eatNewLines(); };

mode INDEXER_MODE;

Indexer:            '[' { popMode(); eatNewLines(); };

mode COLON_MODE;

KeyColon:           ':' { popMode(); eatNewLines(); };

mode STRING_INTERPOLATION_MODE;

RDQuote:            '"' { popMode(); detectIndexer(); };
RDQString:          StringInterpolation+ -> type(String);
RDQInterpBegin:     '#{' { beginInterpolation(); };

mode REGEX_INTERPOLATION_MODE;

RRegex:             '/' { popMode(); pushMode(REGEX_OPTIONS_MODE); };
RegexString1:       ~'\\' { inputAt(1) == '#' && inputAt(2) == '{' }? -> type(String);
RegexString2:       EscapeSequence -> more;
RegexString3:       '\\/' -> more;
RegexString4:       NotInterpolation -> more;
RegexString5:       '#{' { beginInterpolation(); };
RegexString6:       . { inputAt(1) == '/' }? -> type(String);
RegexString7:       . -> more;

mode REGEX_OPTIONS_MODE;

RegexOptions:       [misx]+ -> popMode;

mode STRING_MODE;

RQString:           StringCharacter+ -> type(String);
RQuote:             '\'' { popMode(); detectIndexer(); };

mode NOWDOC_MODE;

RNowDoc:            '\'\'\'' -> popMode;
NowDocString1:      . { inputAt(1) == '\'' && inputAt(2) == '\'' && inputAt(3) == '\'' }? -> type(NowDoc);
NowDocString2:      . -> more;

mode HEREDOC_MODE;

RHereDoc:           '"""' -> popMode;
RHDString1:         ~'\\' { inputAt(1) == '#' && inputAt(2) == '{' }? -> type(String);
RHDString2:         EscapeSequence -> more;
RHDString4:         NotInterpolation -> more;
RHDString5:         '#{' { beginInterpolation(); };
RHDString3:         . { inputAt(1) == '"' && inputAt(2) == '"' && inputAt(3) == '"' }? -> type(String);
RHDString6:         . -> more;

mode EAT_NEWLINE_MODE;

EatNewLine1:        [ \t\u000C]+ -> skip;
EatNewLine2:        [\r\n]+ { skip(); popMode(); eatNewLines(); };
