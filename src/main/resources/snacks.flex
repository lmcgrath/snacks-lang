package snacks.lang.compiler;

import static java.lang.Character.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.countMatches;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static snacks.lang.compiler.Terminals.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import beaver.Symbol;

%%

%class Scanner
%function nextToken
%type Symbol
%extends AbstractScanner

%unicode
%line
%char
%column
%public
%table
%pack

%eofval{
    return token(EOF);
%eofval}

%{
    public Scanner(InputStream stream, String source) throws IOException {
        this(new InputStreamReader(stream, "UTF-8"));
        this.source = source;
    }

    public Scanner(URL source) throws IOException {
        this(new InputStreamReader(source.openStream(), "UTF-8"));
        this.source = source.toString();
    }

    @Override
    public void close() {
        try {
            zzReader.close();
        } catch (IOException exception) {
            // intentionally empty
        }
    }

    @Override
    public int line() {
        return yyline;
    }

    @Override
    public int column() {
        return yycolumn;
    }

    @Override
    public int length() {
        return yylength();
    }
%}

NewLine                 = \r | \n | \r\n
InputCharacter          = [^\r\n]
Comment                 = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment      = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment        = "//" {InputCharacter}* {NewLine}
DocumentationComment    = "/**" {CommentContent} "*"+ "/"
CommentContent          = ([^*] | \*+ [^/*])*
Identifier              = ([:jletter:] | '_') ([:jletterdigit:] | '_')* [\?!]?
Integer                 = "0" | [1-9][0-9]*
Double                  = {Integer}? \.[0-9]+
Whitespace              = [ \t\f]+
UnicodeEscape           = \\u+[a-fA-F0-9]{4}
OctalEscape             = \\([0-3][0-7])?[0-7]
AsciiEscape             = \\[btnfr\"\'\\]
EscapeSequence          = {UnicodeEscape} | {AsciiEscape}
Character               = {EscapeSequence} | [^\n\r']
NotInterpolation        = "#" ~"{" | "\\#{"
InterpolationCharacter  = [^\n\r\"\\#] | {EscapeSequence} | {NotInterpolation}
StringCharacter         = [^\n\r\']
RegexCharacter          = [^#/] | "\\/" | {NotInterpolation} | {Whitespace}
Symbol                  = {Identifier} =? | "+"  | "-"  | "*"   | "%"  | "|"  | "&"  | "^"  | "?"
                                          | "<"  | ">"  | "~"   | ">=" | "<=" | "<<" | ">>" | ">>>"
                                          | "[]" | "**" | "[]=" | ".=" | "in" | "=="
FunctionArgument        = {Identifier} | [:\.] | {Whitespace} | {NewLine}
AnyWhitespace           = {Whitespace} | {NewLine}

%state EAT_NEWLINE_STATE
%state REGEX_STATE
%state REGEX_OPTIONS_STATE
%state STRING_STATE
%state NOWDOC_STATE
%state HEREDOC_STATE
%state INTERPOLATION_STATE
%state SELECTOR_STATE
%state DETECT_FUNCTION_STATE
%state FUNCTION_STATE
%state ANNOTATION_STATE
%state EMBRACE_STATE

%%

<YYINITIAL> {
    "r/"            { enterState(REGEX_STATE); return token(LREGEX); }
    {Symbol} ":"    { detectNewLine(); return token(KEY_SYMBOL, yytext().substring(0, yylength() - 1)); }
    ":" {Symbol}    { detectSelector(); return token(SYMBOL, yytext().substring(1)); }
    {Comment}       { /* ignore */ }
    "<>" | "!="     { detectNewLine(); return token(NOT_EQUALS); }
    "True"          { detectSelector(); return token(TRUE); }
    "False"         { detectSelector(); return token(FALSE); }
    "Nothing"       { detectSelector(); return token(NOTHING); }
    "and" | "&&"    { detectNewLine(); return token(AND); }
    "as"            { return token(AS); }
    "begin"         { detectNewLine(); return token(BEGIN); }
    "c"+ "'" {OctalEscape} "'"
                    {
                        String text = yytext();
                        detectSelector();
                        return token(CHARACTER, unescapeJava(octalToHex(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\'')))).charAt(0));
                    }
    "c"+ "'" {Character} "'"
                    {
                        String text = yytext();
                        detectSelector();
                        return token(CHARACTER, unescapeJava(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\''))).charAt(0));
                    }
    "do"            { return token(DO); }
    "else if"       { return token(ELSE_IF); }
    "else unless"   { return token(ELSE_UNLESS); }
    "else"          { detectNewLine(); return token(ELSE); }
    "if"            { return token(IF); }
    "in"            { detectNewLine(); return token(IN); }
    "is"            { detectNewLine(); return token(IS); }
    "is not"        { detectNewLine(); return token(IS_NOT); }
    "unless"        { return token(UNLESS); }
    "embrace"       { enterState(EMBRACE_STATE); return token(EMBRACE); }
    "end"           { return token(END); }
    "ensure"        { detectNewLine(); return token(ENSURE); }
    "for"           { return token(FOR); }
    "from"          { return token(FROM); }
    "hurl"          { return token(HURL); }
    "import"        { return token(IMPORT); }
    "not in"        { detectNewLine(); return token(NOT_IN); }
    "not" | "!"     { detectNewLine(); return token(NOT); }
    "or" | "||"     { detectNewLine(); return token(OR); }
    "return"        { return token(RETURN); }
    "then"          { detectNewLine(); return token(THEN); }
    "until"         { return token(UNTIL); }
    "using"         { return token(USING); }
    "var"           { return token(VAR); }
    "while"         { return token(WHILE); }
    "_"             { return token(THROWAWAY); }
    "$"             { detectNewLine(); return token(APPLY); }
    {Identifier}    { detectSelector(); return token(IDENTIFIER, yytext()); }
    ";"             { detectNewLine(); return token(SEMICOLON); }
    "::"            { detectNewLine(); return token(DOUBLE_COLON); }
    ":"             { detectNewLine(); return token(COLON); }
    "~"             { detectNewLine(); return token(BIT_NOT); }
    "=="            { detectNewLine(); return token(EQUALS); }
    "=>"            { detectNewLine(); return token(GOES_TO); }
    "="             { detectNewLine(); return token(ASSIGN); }
    "<="            { detectNewLine(); return token(LESS_THAN_EQUALS); }
    "<<"            { detectNewLine(); return token(LSHIFT); }
    "<"             { detectNewLine(); return token(LESS_THAN); }
    ">="            { detectNewLine(); return token(GREATER_THAN_EQUALS); }
    ">>"            { detectNewLine(); return token(RSHIFT); }
    ">>>"           { detectNewLine(); return token(URSHIFT); }
    ">"             { detectNewLine(); return token(GREATER_THAN); }
    "->"            { detectNewLine(); return token(APPLIES_TO); }
    "..."           { detectNewLine(); return token(XRANGE); }
    ".."            { detectNewLine(); return token(RANGE); }
    "."             { detectNewLine(); return token(DOT); }
    "**"            { detectNewLine(); return token(EXPONENT); }
    "*"             { detectNewLine(); return token(MULTIPLY); }
    "/"             { detectNewLine(); return token(DIVIDE); }
    "%"             { detectNewLine(); return token(MODULO); }
    "+"             { detectNewLine(); return token(PLUS); }
    "-"             { detectNewLine(); return token(MINUS); }
    "&"             { detectNewLine(); return token(BIT_AND); }
    "|"             { detectNewLine(); return token(BIT_OR); }
    "^"             { detectNewLine(); return token(BIT_XOR); }
    "?"             { detectNewLine(); return token(COALESCE); }
    "("             { detectFunction(); detectNewLine(); return token(LPAREN); }
    ")"             { detectSelector(); return token(RPAREN); }
    "["             { detectNewLine(); return token(LSQUARE); }
    "]"             { detectSelector(); return token(RSQUARE); }
    "{"             { bracesUp(); detectFunction(); detectNewLine(); return token(LCURLY); }
    "}"             {
                        bracesDown();
                        if (endOfInterpolation()) {
                            return token(RINTERPOLATE);
                        } else {
                            detectSelector();
                            return token(RCURLY);
                        }
                    }
    ","             { detectNewLine(); return token(COMMA); }
    "@"             { enterState(ANNOTATION_STATE); return token(AT); }
    "\"\"\"" \n?    { beginString(HEREDOC_STATE); return token(TRIPLE_DQUOTE); }
    "'''" \n?       { beginString(NOWDOC_STATE); return token(TRIPLE_QUOTE); }
    "\""            { beginString(INTERPOLATION_STATE); return token(DQUOTE); }
    "'"             { beginString(STRING_STATE); return token(QUOTE); }
    {Double}        { detectSelector(); return token(DOUBLE, Double.parseDouble(yytext())); }
    {Integer}       { detectSelector(); return token(INTEGER, Integer.parseInt(yytext())); }
    "\\" {NewLine}+ { /* ignore */ }
    {Whitespace}    { /* ignore */ }
    {NewLine}+      { return token(NEWLINE); }
}

<SELECTOR_STATE> {
    "["             { leaveState(); return token(LINDEX); }
    "("             { leaveState(); return token(LARG); }
    . | {NewLine}   { leaveState(); yypushback(1); }
}

<INTERPOLATION_STATE> {
    "\""            {
                        if (hasString()) {
                            yypushback(1);
                            return string();
                        } else {
                            leaveState();
                            detectSelector();
                            return token(DQUOTE);
                        }
                    }
    "#{"            {
                        if (hasString()) {
                            yypushback(2);
                            return string();
                        } else {
                            beginInterpolation();
                            return token(LINTERPOLATE);
                        }
                    }
    {OctalEscape}   { octalToHex(); }
    {InterpolationCharacter}
                    { gatherString(); }
    "\\" .          { badInput(); }
}

<STRING_STATE> {
    "'"             {
                        if (hasString()) {
                            yypushback(1);
                            return rawString();
                        } else {
                            leaveState();
                            detectSelector();
                            return token(QUOTE);
                        }
                    }
    {StringCharacter}
                    { gatherString(); }
}

<REGEX_STATE> {
    "/"             {
                        if (hasString()) {
                            yypushback(1);
                            return rawString();
                        } else {
                            flipState(REGEX_OPTIONS_STATE);
                            return token(RREGEX);
                        }
                    }
    "#{"            {
                        if (hasString()) {
                            yypushback(2);
                            return rawString();
                        } else {
                            beginInterpolation();
                            return token(LINTERPOLATE);
                        }
                    }
    {RegexCharacter} | {NewLine}
                    { gatherString(); }
}

<REGEX_OPTIONS_STATE> {
    [misx]+         {
                        leaveState();
                        detectSelector();
                        Set<Character> options = new HashSet<>();
                        for (char c : yytext().toCharArray()) {
                            options.add(c);
                        }
                        return token(REGEX_OPTIONS, options);
                    }
    .               { leaveState(); yypushback(1); detectSelector(); return token(REGEX_OPTIONS, new HashSet<Character>()); }
}

<EAT_NEWLINE_STATE> {
    [ \t\f]+        { /* ignore */ }
    {NewLine}+      { leaveState(); detectNewLine(); }
    .               { yypushback(1); leaveState(); }
}

<NOWDOC_STATE> {
    "'''"           {
                        if (hasString()) {
                            yypushback(3);
                            return rawString();
                        } else {
                            leaveState();
                            return token(TRIPLE_QUOTE);
                        }
                    }
    "'"             { gatherString(); }
    {StringCharacter} | {NewLine}
                    { gatherString(); }
}

<HEREDOC_STATE> {
    "\"\"\""        {
                        if (hasString()) {
                            yypushback(3);
                            return string();
                        } else {
                            leaveState();
                            return token(TRIPLE_DQUOTE);
                        }
                    }
    "\""            { gatherString(); }
    "#{"            {
                        if (hasString()) {
                            yypushback(2);
                            return string();
                        } else {
                            beginInterpolation();
                            return token(LINTERPOLATE);
                        }
                    }
    {OctalEscape}   { octalToHex(); }
    {InterpolationCharacter} | {NewLine}
                    { gatherString(); }
    "\\" .          { badInput(); }
}

<DETECT_FUNCTION_STATE> {
    {FunctionArgument}+ [\)\}] {FunctionArgument}* "->"
                    { yypushback(yylength()); flipState(FUNCTION_STATE); }
    {FunctionArgument}+ "->"
                    { yypushback(yylength()); flipState(FUNCTION_STATE); }
    .               { yypushback(1); leaveState(); }
}

<FUNCTION_STATE> {
    "::"            { return token(DOUBLE_COLON); }
    ":"             { return token(COLON); }
    {Identifier}    { return token(FWORD, yytext()); }
    "."             { return token(DOT); }
    ")"             { return token(RPAREN); }
    "}"             { return token(RCURLY); }
    "->"            { leaveState(); detectNewLine(); return token(APPLIES_TO); }
    {AnyWhitespace} { /* ignore */ }
}

<ANNOTATION_STATE> {
    {Identifier}    { return token(IDENTIFIER, yytext()); }
    "."             { return token(DOT); }
    {Whitespace}    { /* ignore */ }
    "("             { leaveState(); return token(LPAREN); }
    .               { yypushback(1); leaveState(); }
}

<EMBRACE_STATE> {
    {Identifier}    { return token(IDENTIFIER, yytext()); }
    "."             { return token(DOT); }
    ":"             { return token(COLON); }
    "->"            { leaveState(); detectNewLine(); return token(APPLIES_TO); }
    {Whitespace}    { /* ignore */ }
}

. { badInput(); }
