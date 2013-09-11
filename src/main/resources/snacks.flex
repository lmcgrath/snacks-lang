package snacks.lang.parser;

import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static snacks.lang.parser.Terminals.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
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
Letter                  = [A-Za-z_\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u1FFF\u2200-\u22FF\u27C0-\u27EF\u2980-\u2AFF\u3040-\u318F\u3300-\u337F\u3400-\u3D2D\u4E00-\u9FFF\uF900-\uFAFF]
Digit                   = [0-9]
IdDigit                 = [0-9\u0660-\u0669\u06F0-\u06F9\u0966-\u096F\u09E6-\u09EF\u0A66-\u0A6F\u0AE6-\u0AEF\u0B66-\u0B6F\u0BE7-\u0BEF\u0C66-\u0C6F\u0CE6-\u0CEF\u0D66-\u0D6F\u0E50-\u0E59\u0ED0-\u0ED9\u1040-\u1049]
IdSymbol                = ("~" | "!" | "$" | "%" | "^" | "&" | "*" | "-" | "=" | "+" | "/" | "?" | "<" | ">")
Id                      = ({IdDigit} | {Letter} | {IdSymbol})+ | "..." | ".." | "[]"
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
FunctionArgument        = {Id} | [:\.] | {Whitespace} | {NewLine}
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
%state EMBRACE_STATE
%state FOR_STATE

%%

<YYINITIAL> {
    {Double}        { detectSelector(); return token(DOUBLE, Double.parseDouble(yytext())); }
    {Integer}       { detectSelector(); return token(INTEGER, Integer.parseInt(yytext())); }
    [\+\-~!] ({Letter} | {IdDigit} | "(" | ")" | "[" | "]" | "{" | "}")
                    { yypushback(yylength() - 1); return token(IDENTIFIER, yytext()); }
    "."             { return token(DOT); }
    "r/"            { enterState(REGEX_STATE); return token(LREGEX); }
    "`" {Id} "`"    { return token(QUOTED_IDENTIFIER, yytext().substring(1, yylength() - 1)); }
    {Id} ":"        { detectNewLine(); return token(KEY_SYMBOL, yytext().substring(0, yylength() - 1)); }
    ":" {Id}        { detectSelector(); return token(SYMBOL, yytext().substring(1)); }
    {Comment}       { /* ignore */ }
    "True"          { detectSelector(); return token(TRUE); }
    "False"         { detectSelector(); return token(FALSE); }
    "Nothing"       { detectSelector(); return token(NOTHING); }
    "affix right"   { return token(PREFIX); }
    "as"            { return token(AS); }
    "begin"         { detectNewLine(); return token(BEGIN); }
    "break"         { return token(BREAK); }
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
    "continue"      { return token(CONTINUE); }
    "data"          { return token(DATA); }
    "do"            { detectNewLine(); return token(DO); }
    "else if"       { return token(ELSE_IF); }
    "else unless"   { return token(ELSE_UNLESS); }
    "else"          { detectNewLine(); return token(ELSE); }
    "if"            { return token(IF); }
    "in"            { detectNewLine(); return token(IDENTIFIER, yytext()); }
    "is"            { detectNewLine(); return token(IDENTIFIER, yytext()); }
    "is not"        { detectNewLine(); return token(IDENTIFIER, yytext()); }
    "unless"        { return token(UNLESS); }
    "embrace"       { enterState(EMBRACE_STATE); return token(EMBRACE); }
    "end"           { return token(END); }
    "ensure"        { detectNewLine(); return token(ENSURE); }
    "for"           { enterState(FOR_STATE); return token(FOR); }
    "from"          { return token(FROM); }
    "hurl"          { return token(HURL); }
    "import"        { return token(IMPORT); }
    "infix left"    { return token(LEFT_INFIX); }
    "infix right"   { return token(RIGHT_INFIX); }
    "infix none"    { return token(INFIX); }
    "not in"        { return token(IDENTIFIER, yytext()); }
    "return"        { return token(RETURN); }
    "then"          { detectNewLine(); return token(THEN); }
    "until"         { return token(UNTIL); }
    "use"           { return token(USE); }
    "var"           { return token(VAR); }
    "while"         { return token(WHILE); }
    {Id}            { return word(); }
    ";"             { detectNewLine(); return token(SEMICOLON); }
    "::"            { detectNewLine(); return token(DOUBLE_COLON); }
    ":"             { detectNewLine(); return token(COLON); }
    "("             { detectFunction(); yypushback(1); }
    ")"             { detectSelector(); return token(RPAREN); }
    "["             { detectNewLine(); return token(LSQUARE); }
    "]"             { detectSelector(); return token(RSQUARE); }
    "{"             { bracesUp(); detectFunction(); yypushback(1); }
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
    "\"\"\"" \n?    { beginString(HEREDOC_STATE); return token(TRIPLE_DQUOTE); }
    "'''" \n?       { beginString(NOWDOC_STATE); return token(TRIPLE_QUOTE); }
    "\""            { beginString(INTERPOLATION_STATE); return token(DQUOTE); }
    "'"             { beginString(STRING_STATE); return token(QUOTE); }
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
    "\\'"           { string.append('\''); }
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
    "(" {FunctionArgument}* ")" {FunctionArgument}* "->"
                    { yypushback(yylength()); flipState(FUNCTION_STATE); }
    [\(\{] {FunctionArgument}* "->"
                    { yypushback(yylength()); flipState(FUNCTION_STATE); }
    "("             { leaveState(); detectNewLine(); return token(LPAREN); }
    "{"             { leaveState(); detectNewLine(); return token(LCURLY); }
    .               { yypushback(1); leaveState(); }
}

<FUNCTION_STATE> {
    "("             { return token(LFUNC); }
    "{"             { return token(LFUNC_MULTILINE); }
    "::"            { return token(DOUBLE_COLON); }
    ":"             { return token(COLON); }
    "`" {Id} "`"    { return token(FWORD, yytext().substring(1, yylength() - 1)); }
    {Id}            { return fword(); }
    "."             { return token(DOT); }
    ")"             { return token(RPAREN); }
    "}"             { return token(RCURLY); }
    {AnyWhitespace} { /* ignore */ }
}

<EMBRACE_STATE> {
    ":"             { return token(COLON); }
    "`" {Id} "`"    { return token(FWORD, yytext().substring(1, yylength() - 1)); }
    {Id}            { return eword(); }
    "."             { return token(DOT); }
    {Whitespace}    { /* ignore */ }
}

<FOR_STATE> {
    "in"            { leaveState(); return token(IN); }
    {Id}            { return word(); }
}

. { badInput(); }
