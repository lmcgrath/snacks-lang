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
%extends beaver.Scanner
%implements AutoCloseable

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
    private final Deque<Integer> braces = new ArrayDeque<>();
    private final Deque<Integer> states = new ArrayDeque<>();
    private final StringBuilder string = new StringBuilder();

    private String source = "NULL";

    { states.push(YYINITIAL); }

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

    private void badInput() throws IOException {
        String message = "Unexpected input '" + yytext() + "'";
        error(message);
        throw new IOException(message);
    }

    private void beginInterpolation() {
        enterState(YYINITIAL);
        braces.push(0);
    }

    private void beginString(int state) {
        enterState(state);
        string.setLength(0);
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

    private void detectFunction() {
        enterState(DETECT_FUNCTION_STATE);
    }

    private void detectNewLine() {
        enterState(EAT_NEWLINE_STATE);
    }

    private void detectSelector() {
        enterState(SELECTOR_STATE);
    }

    private void enterState(int state) {
        states.push(state);
        yybegin(state);
    }

    private void flipState(int state) {
        states.pop();
        enterState(state);
    }

    private void leaveState() {
        states.pop();
        yybegin(states.peek());
    }

    private void error(String message) {
        throw new ScanException(message + " in " + source + " (" + yyline + ", " + yycolumn + ")");
    }

    private boolean endOfInterpolation() {
        if (!braces.isEmpty() && braces.peek() < 0) {
            leaveState();
            braces.pop();
            return true;
        } else {
            return false;
        }
    }

    private boolean hasString() {
        return string.length() > 0;
    }

    private void octalToHex() {
        string.append(octalToHex(yytext()));
    }

    private String octalToHex(String text) {
        return String.format("\\u%04x", Integer.parseInt(text.substring(1), 8));
    }

    private Symbol rawString() {
        String value = string.toString();
        string.setLength(0);
        return token(STRING, value);
    }

    private Symbol string() {
        String value = string.toString();
        string.setLength(0);
        return token(STRING, unescapeJava(value.replaceAll("\\\\u+", "\\\\u")));
    }

    private Symbol token(short type) {
        return new Token(type, yyline, yycolumn, yylength(), source);
    }

    private Symbol token(short type, Object value) {
        return new Token(type, yyline, yycolumn, yylength(), source, value);
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
    //"begin"         { detectNewLine(); return token(BEGIN); }
    [Cc] "'" {OctalEscape} "'"
                    {
                        String text = yytext();
                        detectSelector();
                        return token(CHARACTER, unescapeJava(octalToHex(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\'')))).charAt(0));
                    }
    [Cc] "'" {Character} "'"
                    {
                        String text = yytext();
                        detectSelector();
                        return token(CHARACTER, unescapeJava(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\''))).charAt(0));
                    }
/*
    "else if"       { return token(ELSE_IF); }
    "else unless"   { return token(ELSE_UNLESS); }
    "else"          { detectNewLine(); return token(ELSE); }
    "if"            { return token(IF); }
*/
    "in"            { detectNewLine(); return token(IN); }
    "is"            { detectNewLine(); return token(IS); }
    "is not"        { detectNewLine(); return token(IS_NOT); }
/*
    "unless"        { return token(UNLESS); }
    "embrace"       { detectNewLine(); return token(EMBRACE); }
    "end"           { return token(END); }
    "ensure"        { detectNewLine(); return token(ENSURE); }
    "for"           { return token(FOR); }
*/
    "from"          { return token(FROM); }
    //"hurl"          { return token(HURL); }
    "import"        { return token(IMPORT); }
    "not in"        { detectNewLine(); return token(NOT_IN); }
    "not" | "!"     { detectNewLine(); return token(NOT); }
    "or" | "||"     { detectNewLine(); return token(OR); }
/*
    "return"        { return token(RETURN); }
    "then"          { detectNewLine(); return token(THEN); }
    "until"         { return token(UNTIL); }
    "using"         { return token(USING); }
    "var"           { return token(VAR); }
    "while"         { return token(WHILE); }
    "_"             { return token(THROWAWAY); }
*/
    "$"             { detectNewLine(); return token(APPLY); }
    {Identifier}    { detectSelector(); return token(IDENTIFIER, yytext()); }
    ";"             { detectNewLine(); return token(SEMICOLON); }
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
    {Whitespace} | {NewLine}   { /* ignore */ }
/*
    {NewLine}+      { return token(NEWLINE); }
*/
}

<SELECTOR_STATE> {
    "["             { leaveState(); return token(LINDEX); }
    "("             { leaveState(); return token(LARG); }
    .               { leaveState(); yypushback(1); }
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
                    { string.append(yytext()); }
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
                    { string.append(yytext()); }
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
                    { string.append(yytext()); }
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
    [ \t\f]+        { }
    {NewLine}       { leaveState(); detectNewLine(); }
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
    "'"             { string.append(yytext()); }
    {StringCharacter} | {NewLine}
                    { string.append(yytext()); }
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
    "\""            { string.append(yytext()); }
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
                    { string.append(yytext()); }
    "\\" .          { badInput(); }
}

<DETECT_FUNCTION_STATE> {
    {FunctionArgument}+ (")" | "}") (":" {FunctionArgument}+)? "->"
                    { yypushback(yylength()); flipState(FUNCTION_STATE); }
    {FunctionArgument}+ "->"
                    { yypushback(yylength()); flipState(FUNCTION_STATE); }
    .               { yypushback(1); leaveState(); }
}

<FUNCTION_STATE> {
    "$"             { return token(DOLLAR); }
    ":"             { return token(COLON); }
    {Identifier}    { return token(FWORD, yytext()); }
    "."             { return token(DOT); }
    ")"             { return token(RPAREN); }
    "}"             { return token(RCURLY); }
    "->"            { leaveState(); return token(APPLIES_TO); }
    {AnyWhitespace} { /* ignore */ }
}

<ANNOTATION_STATE> {
    {Identifier}    { return token(IDENTIFIER, yytext()); }
    "."             { return token(DOT); }
    {Whitespace}    { /* ignore */ }
    "("             { leaveState(); return token(LPAREN); }
    .               { yypushback(1); leaveState(); }
}

. { badInput(); }
