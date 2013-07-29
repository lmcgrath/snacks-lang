package iddic.lang.compiler;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static iddic.lang.compiler.Terminals.*;
import static iddic.lang.util.ReaderUtil.*;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import beaver.Symbol;
import iddic.lang.compiler.syntax.Token;

@SuppressWarnings("unused")

%%

%class Scanner
%function nextToken
%type Symbol
%extends beaver.Scanner
%yylexthrow beaver.Scanner.Exception

%unicode
%line
%column
%public
%table
%pack

%eofval{
    return new Symbol(EOF, "end-of-file");
%eofval}

%{
    private final Deque<Integer> states = new ArrayDeque<>();
    private final StringBuilder string = new StringBuilder();

    private int braces = 0;
    private URL source = null;

    { states.push(YYINITIAL); }

    public Scanner(URL source) {
        this(readerFor(source));
        this.source = source;
    }

    public void enterState(int state) {
        states.push(state);
        yybegin(state);
    }

    public void leaveState() {
        states.pop();
        yybegin(states.peek());
    }

    public void swapState(int state) {
        states.pop();
        enterState(state);
    }

    private void badInput() throws beaver.Scanner.Exception {
        error("Unexpected input '" + yytext() + "'");
    }

    private void error(String message) throws beaver.Scanner.Exception {
        throw new beaver.Scanner.Exception(yyline, yycolumn, message + " in " + source);
    }

    private Symbol token(short type) {
        return new Token(type, yyline, yycolumn, yylength(), source);
    }

    private Symbol token(short type, Object value) {
        return new Token(type, yyline, yycolumn, yylength(), source, value);
    }

    private Symbol transitionSymbol(short type) {
        return transitionSymbol(type, null);
    }

    private Symbol transitionSymbol(short type, Object value) {
        if (yystate() == YYKEY_SYMBOL) {
            swapState(YYKEY);
        } else {
            leaveState();
        }
        return token(type, value);
    }
%}

NewLine              = \r | \n | \r\n
InputCharacter       = [^\r\n]
Comment              = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {NewLine}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ([^*] | \*+ [^/*])*
Identifier           = ([:jletter:] | '_') ([:jletterdigit:] | '_')* [\?!]?
Integer              = "0" | [1-9][0-9]*
Double               = {Integer}? \.[0-9]+
Whitespace           = [ \t\f]+
UnicodeEscape        = \\[uU][a-fA-F0-9]{4}
OctalEscape          = \\[0-3]?[0-7]{2}
AsciiEscape          = \\[btnfr\"\'\\]
Symbol               = {Identifier} =? | "+"   | "-"  | "*"  | "%"  | "|"  | "&"  | "^"  | "?"
                                       | "<"   | ">"  | "~"  | ">=" | "<=" | "<<" | ">>" | ">>>"
                                       | "[]=" | "**" | "[]" | ".=" | "in" | "=="

%state YYSTRING
%state YYSTRING_END
%state YYINTERPOLATION
%state YYINTERPOLATION_END
%state YYEXPRESSION
%state YYCHAR
%state YYHEREDOC
%state YYHEREDOC_END
%state YYNOWDOC
%state YYNOWDOC_END
%state YYREGEX
%state YYREGEX_END
%state YYREGEX_OPTIONS
%state YYLIST
%state YYKEY
%state YYSYMBOL
%state YYKEY_SYMBOL

%%

<YYINITIAL> {
    "{"             { return token(CURLY_BRACE_OPEN); }
    "}"             { return token(CURLY_BRACE_CLOSE); }
}

<YYEXPRESSION> {
    "{"             {
                        braces++;
                        return token(CURLY_BRACE_OPEN);
                    }
    "}"             {
                        if (braces == 0) {
                            leaveState();
                            string.setLength(0);
                        } else {
                            braces--;
                            return token(CURLY_BRACE_CLOSE);
                        }
                    }
}

<YYLIST> {
    {Whitespace}    { /* ignore */ }
    "["             {
                        leaveState();
                        return token(SQUARE_BRACE_OPEN);
                    }
    {NewLine}       { return token(NEWLINE); }
    .               {
                        leaveState();
                        yypushback(1);
                    }
}

<YYINITIAL, YYEXPRESSION> {
    {Comment}       { /* ignore */ }
    ":" {Symbol}    {
                        yypushback(yylength() - 1);
                        enterState(YYSYMBOL);
                        return token(SYMBOL_SIGIL);
                    }
    {Symbol} ":"    {
                        enterState(YYKEY_SYMBOL);
                        yypushback(yylength());
                    }
    "False"         { return token(BOOLEAN, false); }
    "Nothing"       { return token(NOTHING); }
    "True"          { return token(BOOLEAN, true); }
    "and"           { return token(LOGICAL_AND); }
    "as"            { return token(AS); }
    "begin"         { return token(BEGIN); }
    [Cc] \'         {
                        enterState(YYCHAR);
                        string.setLength(0);
                    }
    "else if"       { return token(ELSE_IF); }
    "else unless"   { return token(ELSE_UNLESS); }
    "else"          { return token(ELSE); }
    "embrace"       { return token(EMBRACE); }
    "end"           { return token(END); }
    "ensure"        { return token(ENSURE); }
    "from"          { return token(FROM); }
    "if"            { return token(IF); }
    "import"        { return token(IMPORT); }
    "in"            { return token(IN); }
    "is not"        { return token(IS_NOT); }
    "is"            { return token(IS); }
    "not in"        { return token(NOT_IN); }
    "not"           { return token(LOGICAL_NOT); }
    "or"            { return token(LOGICAL_OR); }
    "return"        { return token(RETURN); }
    "r/"            {
                        enterState(YYREGEX);
                        string.setLength(0);
                        return token(REGEX_OPEN);
                    }
    "then"          { return token(THEN); }
    "unless"        { return token(UNLESS); }
    "var"           { return token(VAR); }
    "'''" {NewLine} {
                        enterState(YYNOWDOC);
                        string.setLength(0);
                        return token(NOWDOC_OPEN);
                    }
    "'''"           {
                        enterState(YYNOWDOC);
                        string.setLength(0);
                        return token(NOWDOC_OPEN);
                    }
    "\"\"\"" {NewLine}
                    {
                        enterState(YYHEREDOC);
                        string.setLength(0);
                        return token(HEREDOC_OPEN);
                    }
    "\"\"\""        {
                        enterState(YYHEREDOC);
                        string.setLength(0);
                        return token(HEREDOC_OPEN);
                    }
    \'              {
                        enterState(YYSTRING);
                        string.setLength(0);
                        return token(SINGLE_QUOTE_OPEN);
                    }
    \"              {
                        enterState(YYINTERPOLATION);
                        string.setLength(0);
                        return token(DOUBLE_QUOTE_OPEN);
                    }
    ";"             { return token(SEMICOLON); }
    "..."           { return token(XRANGE); }
    ".."            { return token(RANGE); }
    "."             { return token(ACCESSOR); }
    "("             {
                        enterState(YYLIST);
                        return token(PARENTHESIS_OPEN);
                    }
    ")"             { return token(PARENTHESIS_CLOSE); }
    {Whitespace} "[]=:"
                    {
                        enterState(YYKEY_SYMBOL);
                        yypushback(4);
                    }
    {Whitespace} "[]:"
                    {
                        enterState(YYKEY_SYMBOL);
                        yypushback(3);
                    }
    {Whitespace} "["
                    {
                        enterState(YYLIST);
                        return token(SQUARE_BRACE_OPEN);
                    }
    "["             { return token(INDEXER_OPEN); }
    "]"             { return token(SQUARE_BRACE_CLOSE); }
    "@:"            { return token(ROOT); }
    "@"             { return token(AT); }
    ","             { return token(COMMA); }
    "=>"            { return token(GOES_TO); }
    "=="            { return token(EQUALS); }
    "="             { return token(ASSIGN); }
    "!="            { return token(NOT_EQUALS); }
    "<>"            { return token(NOT_EQUALS); }
    "<<"            { return token(SHIFT_LEFT); }
    "<="            { return token(LESS_THAN_EQUALS); }
    "<"             { return token(LESS_THAN); }
    ">>>"           { return token(SHIFT_RIGHT); }
    ">>"            { return token(SIGNED_SHIFT_RIGHT); }
    ">="            { return token(GREATER_THAN_EQUALS); }
    ">"             { return token(GREATER_THAN); }
    "$"             { return token(APPLY); }
    "&&"            { return token(LOGICAL_AND); }
    "&"             { return token(BITWISE_AND); }
    "||"            { return token(LOGICAL_OR); }
    "|"             { return token(BITWISE_OR); }
    "^"             { return token(BITWISE_XOR); }
    "~"             { return token(BITWISE_NOT); }
    "+"             { return token(PLUS); }
    "->"            { return token(APPLIES_TO); }
    "-"             { return token(MINUS); }
    "**"            { return token(EXPONENT); }
    "*"             { return token(MULTIPLY); }
    "/"             { return token(DIVIDE); }
    "%"             { return token(MODULO); }
    "?"             { return token(COALESCE); }
    ":"             { return token(COLON); }
    \\ {NewLine}    { /* ignore */ }
    {Double}        { return token(DOUBLE, Double.valueOf(yytext())); }
    {Integer}       { return token(INTEGER, Integer.valueOf(yytext())); }
    {Identifier}    { return token(IDENTIFIER, yytext()); }
    {NewLine}       { return token(NEWLINE); }
    {Whitespace}    { /* ignore */ }
}

<YYSTRING> {
    \'              {
                        swapState(YYSTRING_END);
                        yybegin(YYSTRING_END);
                        yypushback(1);
                        return token(STRING, string.toString());
                    }
    {NewLine}       { error("Unterminated string"); }
    [^\'\\]+        { string.append(yytext()); }
}

<YYINTERPOLATION> {
    \"              {
                        swapState(YYINTERPOLATION_END);
                        yypushback(1);
                        if (string.length() > 0) {
                            return token(STRING, string.toString());
                        }
                    }
    {NewLine}       { error("Unterminated string"); }
    [^\"\\\\#]+     { string.append(yytext()); }
}

<YYNOWDOC> {
    "'''"           {
                        swapState(YYNOWDOC_END);
                        yypushback(3);
                        if (string.length() > 0) {
                            return token(STRING, string.toString());
                        }
                    }
    [^\']+          { string.append(yytext()); }
    "'"             { string.append(yytext()); }
}

<YYHEREDOC> {
    "\"\"\""        {
                        swapState(YYHEREDOC_END);
                        yypushback(3);
                        if (string.length() > 0) {
                            return token(STRING, string.toString());
                        }
                    }
    [^\"\\#]+       { string.append(yytext()); }
    \"              { string.append(yytext()); }
}

<YYREGEX> {
    "/"             {
                        swapState(YYREGEX_END);
                        yypushback(1);
                        if (string.length() > 0) {
                            return token(STRING, string.toString());
                        }
                    }
    [^/\\#]+        { string.append(yytext()); }
    "\\/"           { string.append('/'); }
}

<YYCHAR> {
    \'              {
                        leaveState();
                        char[] chars = string.toString().toCharArray();
                        if (chars.length < 1) {
                            error("Empty character literal");
                        }
                        return token(CHARACTER, chars[chars.length - 1]);
                    }
    [^\'\\]+        { string.append(yytext()); }
}

<YYINTERPOLATION, YYHEREDOC, YYREGEX> {
    "#{"            {
                        enterState(YYEXPRESSION);
                        enterState(YYLIST);
                        if (string.length() > 0) {
                            return token(STRING, string.toString());
                        }
                    }
    \\#             { string.append('#'); }
}

<YYSTRING, YYINTERPOLATION, YYCHAR> {
    \\\"            { string.append('\"'); }
    \\\'            { string.append('\''); }
}

<YYSTRING, YYINTERPOLATION, YYCHAR, YYHEREDOC, YYNOWDOC> {
    {AsciiEscape}   { string.append(unescapeJava(yytext())); }
    {UnicodeEscape} { string.append(unescapeJava(yytext())); }
    {OctalEscape}   { string.append(unescapeJava(yytext())); }
}

<YYINTERPOLATION_END> {
    \"              {
                        leaveState();
                        return token(DOUBLE_QUOTE_CLOSE);
                    }
}

<YYSTRING_END> {
    \'              {
                        leaveState();
                        return token(SINGLE_QUOTE_CLOSE);
                    }
}

<YYNOWDOC_END> {
    "'''"           {
                        leaveState();
                        return token(NOWDOC_CLOSE);
                    }
}

<YYHEREDOC_END> {
    "\"\"\""        {
                        leaveState();
                        return token(HEREDOC_CLOSE);
                    }
}

<YYREGEX_END> {
    "/"             {
                        swapState(YYREGEX_OPTIONS);
                        return token(REGEX_CLOSE);
                    }
}

<YYREGEX_OPTIONS> {
    [mis]+          {
                        Set<String> set = new HashSet<>();
                        String options = yytext();
                        for (String option : asList("m", "i", "s")) {
                            if (options.contains(option)) {
                                set.add(option);
                            }
                        }
                        return token(REGEX_OPTIONS, set);
                    }
    .               {
                        leaveState();
                        yypushback(1);
                    }
}

<YYKEY_SYMBOL, YYSYMBOL> {
    "+"             { return transitionSymbol(PLUS); }
    "-"             { return transitionSymbol(MINUS); }
    "*"             { return transitionSymbol(MULTIPLY); }
    "%"             { return transitionSymbol(MODULO); }
    "|"             { return transitionSymbol(BITWISE_OR); }
    "&"             { return transitionSymbol(BITWISE_AND); }
    "^"             { return transitionSymbol(BITWISE_XOR); }
    "?"             { return transitionSymbol(COALESCE); }
    "<"             { return transitionSymbol(LESS_THAN); }
    ">"             { return transitionSymbol(GREATER_THAN); }
    "~"             { return transitionSymbol(BITWISE_NOT); }
    ">="            { return transitionSymbol(GREATER_THAN_EQUALS); }
    "<="            { return transitionSymbol(SHIFT_LEFT); }
    "<<"            { return transitionSymbol(SHIFT_LEFT); }
    ">>"            { return transitionSymbol(SIGNED_SHIFT_RIGHT); }
    ">>>"           { return transitionSymbol(SHIFT_RIGHT); }
    "in"            { return transitionSymbol(IN); }
    "**"            { return transitionSymbol(EXPONENT); }
    "=="            { return transitionSymbol(EQUALS); }
    "[]="           { return transitionSymbol(SYMBOL, yytext()); }
    "[]"            { return transitionSymbol(SYMBOL, yytext()); }
    ".="            { return transitionSymbol(SYMBOL, yytext()); }
    {Identifier} "="
                    { return transitionSymbol(SYMBOL, yytext()); }
    {Identifier}    { return transitionSymbol(IDENTIFIER, yytext()); }
}

<YYKEY> {
    ":"             {
                        leaveState();
                        return token(COLON);
                    }
}

. { badInput(); }
