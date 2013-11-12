package snacks.lang.parser;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static snacks.lang.parser.Terminals.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;
import snacks.lang.util.Position;

public class Scanner extends beaver.Scanner implements AutoCloseable {

    public static final int INITIAL_BUFFER_SIZE = 1024;
    public static final int READ_BUFFER_SIZE = 1024;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Pattern symbolPattern = Pattern.compile("^[\\W]+$");
    private static final Set<String> newlineWords = new HashSet<>();
    private static final Map<String, Short> dictionary = new HashMap<>();

    static {
        newlineWords.addAll(asList(
            "begin",
            "ensure",
            "->",
            "=>",
            "=",
            "else",
            "where",
            "derives"
        ));
    }

    static {
        dictionary.put("True", TRUE);
        dictionary.put("False", FALSE);
        dictionary.put("as", AS);
        dictionary.put("begin", BEGIN);
        dictionary.put("break", BREAK);
        dictionary.put("continue", CONTINUE);
        dictionary.put("data", DATA);
        dictionary.put("else", ELSE);
        dictionary.put("if", IF);
        dictionary.put("unless", UNLESS);
        dictionary.put("embrace", EMBRACE);
        dictionary.put("end", END);
        dictionary.put("ensure", ENSURE);
        dictionary.put("for", FOR);
        dictionary.put("from", FROM);
        dictionary.put("hurl", HURL);
        dictionary.put("import", IMPORT);
        dictionary.put("return", RETURN);
        dictionary.put("until", UNTIL);
        dictionary.put("use", USE);
        dictionary.put("var", VAR);
        dictionary.put("while", WHILE);
        dictionary.put("->", APPLIES_TO);
        dictionary.put("=>", GOES_TO);
        dictionary.put("_", THROWAWAY);
        dictionary.put("=", ASSIGN);
        dictionary.put("affix", null);
        dictionary.put("infix", null);
        dictionary.put("is", IDENTIFIER);
        dictionary.put("not", IDENTIFIER);
        dictionary.put("protocol", PROTOCOL);
        dictionary.put("implement", IMPLEMENT);
        dictionary.put("derives", DERIVES);
        dictionary.put("where", WHERE);
    }

    private final Deque<Integer> braces;
    private final Reader reader;
    private final String source;
    private final Deque<State> states;
    private final StringBuilder string;
    private char[] data;
    private int position;
    private int length;
    private int line;
    private int column;
    private int start;
    private Position startPosition;
    private Token token;
    private Action action;

    public Scanner(String source, InputStream stream) {
        this.source = source;
        this.braces = new ArrayDeque<>();
        this.reader = new InputStreamReader(stream, UTF_8);
        this.states = new ArrayDeque<>(asList(State.DEFAULT));
        this.string = new StringBuilder();
        this.data = new char[INITIAL_BUFFER_SIZE];
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException exception) {
            // intentionally empty
        }
    }

    public int lookAhead(int offset) {
        buffer();
        if (position + offset >= length) {
            return -1;
        } else {
            return data[position + offset];
        }
    }

    public Token nextToken() {
        startPosition = markStartPosition();
        if (peek() == -1) {
            return new Token(EOF, currentPosition());
        }
        while (true) {
            token = null;
            action = Action.ERROR;
            start = position;
            switch (state()) {
                case SUFFIX:
                    action = scanSuffix();
                    break;
                case STRING:
                    action = scanString();
                    break;
                case NOWDOC:
                    action = scanNowdoc();
                    break;
                case DEFAULT:
                    action = scanDefault();
                    break;
                case INTERPOLATION:
                    action = scanInterpolation();
                    break;
                case HEREDOC:
                    action = scanHeredoc();
                    break;
                case FUNCTION:
                    action = scanFunction();
                    break;
                case SKIP_NEWLINES:
                    action = skipNewlines();
                    break;
                case EMBRACE:
                    action = scanEmbrace();
                    break;
                case QUOTED_OPERATOR:
                    action = scanQuotedOperator();
                    break;
                case QUOTED_IDENTIFIER:
                    action = scanQuotedIdentifier();
                    break;
                case DATA_DECLARATION:
                    action = scanDataDeclaration();
                    break;
                case TYPE_SIGNATURE:
                    action = scanTypeSignature();
                    break;
                default:
                    throw new ParseException("Unhandled state: " + state());
            }
            switch (action) {
                case KEEP_GOING:
                    continue;
                case ACCEPT:
                    return token;
                case ERROR:
                    throw unexpected();
                default:
                    throw new ParseException("Unhandled action: " + action);
            }
        }
    }

    private Action accept(short kind) {
        return accept(kind, text());
    }

    private Action accept(short kind, Object value) {
        token = new Token(kind, value, new Position(startPosition, line, column));
        return Action.ACCEPT;
    }

    private void beginInterpolation() {
        enterState(State.DEFAULT);
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

    private void buffer(int offset) {
        if (position + offset >= length) {
            int numRead;
            int position = 0;
            if (position + READ_BUFFER_SIZE > data.length) {
                data = copyOf(data, data.length * 2);
            }
            try {
                numRead = reader.read(data, position, READ_BUFFER_SIZE);
            } catch (IOException exception) {
                throw new ParseException(exception);
            }
            if (numRead != -1) {
                length += numRead;
            }
        }
    }

    private void buffer() {
        buffer(0);
    }

    private Position currentPosition() {
        return new Position(source, line, column);
    }

    private Action detectFunctionMultiline() {
        boolean function = false;
        try (LookAhead ignored = new LookAhead()) {
            while (isIdentifier(peek()) || peek() == ':' || peek() == '.' || isWhitespace(peek())) {
                if (peek() == '-' && lookAhead(1) == '>') {
                    function = true;
                    break;
                }
                read();
            }
        }
        if (function) {
            enterState(State.FUNCTION);
            return accept(LFUNC_MULTILINE);
        } else {
            detectNewlines();
            return accept(LCURLY);
        }
    }

    private Action detectFunctionParen() {
        boolean function = false;
        int parens = 1;
        try (LookAhead ignored = new LookAhead()) {
            while (isIdentifier(peek()) || peek() == ')' || peek() == ':' || peek() == '.' || isWhitespace(peek())) {
                if (peek() == ')') {
                    if (parens == 0) {
                        function = false;
                        break;
                    }
                    parens--;
                }
                if (peek() == '-' && lookAhead(1) == '>') {
                    function = true;
                    break;
                }
                read();
            }
        }
        if (function) {
            enterState(State.FUNCTION);
            return accept(LFUNC);
        } else {
            detectNewlines();
            return accept(LPAREN);
        }
    }

    private void detectNewlines() {
        if (isWhitespace(peek())) {
            int i = -1;
            while (true) {
                if (isWhitespace(lookAhead(++i))) {
                    if (lookAhead(i) == '\n') {
                        enterState(State.SKIP_NEWLINES);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void detectSuffix() {
        if (peek() == '[') {
            enterState(State.SUFFIX);
        }
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

    private void enterState(State state) {
        states.push(state);
    }

    private Action error() {
        return action = Action.ERROR;
    }

    private boolean expect(String word) {
        for (int i = 1; i < word.length(); i++) {
            if (lookAhead(i) != word.charAt(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private boolean expectQuotedIdentifier() {
        try (LookAhead ignore = new LookAhead()) {
            if ((peek() == '-' || peek() == '+' || peek() == '~' || peek() == '!') && (isLetter(lookAhead(1)) || isDigit(lookAhead(1)))) {
                return false;
            }
            if (isQuoted(peek())) {
                while (isQuoted(peek())) {
                    read();
                }
                if (peek() == ')') {
                    read();
                    while (isWhitespace(peek())) {
                        read();
                    }
                    return !(peek() == '-' && lookAhead(1) == '>');
                }
            }
            return false;
        }
    }

    private boolean hasString() {
        return string.length() > 0;
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private boolean isHex(int c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'F'
            || c >= 'a' && c <= 'f';
    }

    private boolean isIdentifier(int c) {
        return isLetter(c) || isIdentifierDigit(c) || isSymbol(c);
    }

    private boolean isIdentifierDigit(int c) {
        return isDigit(c)
            || c >= '\u0660' && c <= '\u0669'
            || c >= '\u06F0' && c <= '\u06F9'
            || c >= '\u0966' && c <= '\u096F'
            || c >= '\u09E6' && c <= '\u09EF'
            || c >= '\u0A66' && c <= '\u0A6F'
            || c >= '\u0AE6' && c <= '\u0AEF'
            || c >= '\u0B66' && c <= '\u0B6F'
            || c >= '\u0BE7' && c <= '\u0BEF'
            || c >= '\u0C66' && c <= '\u0C6F'
            || c >= '\u0CE6' && c <= '\u0CEF'
            || c >= '\u0D66' && c <= '\u0D6F'
            || c >= '\u0E50' && c <= '\u0E59'
            || c >= '\u0ED0' && c <= '\u0ED9'
            || c >= '\u1040' && c <= '\u1049';
    }

    private boolean isLetter(int c) {
        return c >= 'A' && c <= 'Z'
            || c >= 'a' && c <= 'z'
            || c == '_'
            || c >= '\u00C0' && c <= '\u00D6'
            || c >= '\u00D8' && c <= '\u00F6'
            || c >= '\u00F8' && c <= '\u1FFF'
            || c >= '\u2200' && c <= '\u22FF'
            || c >= '\u27C0' && c <= '\u27EF'
            || c >= '\u2980' && c <= '\u2AFF'
            || c >= '\u3040' && c <= '\u318F'
            || c >= '\u3300' && c <= '\u337F'
            || c >= '\u3400' && c <= '\u3D2D'
            || c >= '\u4E00' && c <= '\u9FFF'
            || c >= '\uF900' && c <= '\uFAFF';
    }

    private boolean isOpeningSeparator(int c) {
        return c == '(' || c == '[' || c == '{';
    }

    private boolean isPrefix(int c) {
        return c == '+' || c == '-' || c == '!' || c == '~';
    }

    private boolean isQuoted(int c) {
        return isIdentifier(c) || c == '[' || c == ']' || c == '.';
    }

    private boolean isSymbol(int c) {
        return c == '~'
            || c == '!'
            || c == '$'
            || c == '%'
            || c == '^'
            || c == '&'
            || c == '*'
            || c == '-'
            || c == '='
            || c == '+'
            || c == '/'
            || c == '?'
            || c == '<'
            || c == '>';
    }

    private boolean isWhitespace(int c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f';
    }

    private Action keepGoing() {
        return action = Action.KEEP_GOING;
    }

    private void leaveState() {
        states.pop();
    }

    private Position markStartPosition() {
        return new Position(source, line, column);
    }

    private void octalToHex() {
        string.append(octalToHex(text()));
    }

    private String octalToHex(String text) {
        return String.format("\\u%04x", Integer.parseInt(text.substring(1), 8));
    }

    private int peek() {
        buffer();
        return position < length ? data[position] : -1;
    }

    private boolean range(int lower, int upper) {
        if (peek() >= lower && peek() <= upper) {
            read();
            return true;
        } else {
            return false;
        }
    }

    private Action rawString() {
        String value = string.toString();
        string.setLength(0);
        return accept(STRING, value);
    }

    private void read(int amount) {
        for (int i = 0; i < amount; i++) {
            read();
        }
    }

    private void read() {
        if (position <= length) {
            if (peek() == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }
            position++;
        } else {
            throw new ParseException("Read past EOF in " + currentPosition());
        }
    }

    private void require(int c) {
        if (peek() == c) {
            read();
        } else {
            throw unexpected();
        }
    }

    private void requireHex() {
        if (isHex(peek())) {
            read();
        } else {
            throw unexpected();
        }
    }

    private Action scanCharacter() {
        while (peek() == 'c') {
            read();
        }
        if (peek() == '\'') {
            read();
            if (peek() == '\'' || peek() == '\n' || peek() == '\r') {
                return error();
            } else if (peek() == '\\') {
                read();
                if (scanEscapeSequence()) {
                    require('\'');
                    String text = text();
                    detectSuffix();
                    return accept(CHARACTER, unescapeJava(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\''))).charAt(0));
                } else if (scanOctal()) {
                    require('\'');
                    String text = text();
                    detectSuffix();
                    return accept(CHARACTER, unescapeJava(octalToHex(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\'')))).charAt(0));
                }
            }
            read();
            require('\'');
            String text = text();
            detectSuffix();
            return accept(CHARACTER, unescapeJava(text.substring(text.indexOf('\'') + 1, text.lastIndexOf('\''))).charAt(0));
        }
        return keepGoing();
    }

    private Action scanColon() {
        require(':');
        if (peek() == ':') {
            read();
            detectNewlines();
            enterState(State.TYPE_SIGNATURE);
            return accept(DOUBLE_COLON);
        } else if (isIdentifier(peek())) {
            while (isIdentifier(peek())) {
                read();
            }
            detectSuffix();
            return accept(SYMBOL, text().substring(1));
        } else if (peek() == '[' && lookAhead(1) == ']') {
            detectSuffix();
            return accept(SYMBOL, text().substring(1));
        } else if (peek() == '\'' || peek() == '"') {
            return accept(SYMBOL_CONSTRUCTOR);
        } else {
            detectNewlines();
            return accept(COLON);
        }
    }

    private Action scanDataDeclaration() {
        switch (peek()) {
            case '-':
                read();
                require('>');
                detectNewlines();
                return accept(APPLIES_TO);
            case '(':
                read();
                if (peek() == ')') {
                    read();
                    return accept(UNIT);
                }
                detectNewlines();
                return accept(LPAREN);
            case ')':
                read();
                return accept(RPAREN);
            case ',':
                read();
                detectNewlines();
                return accept(COMMA);
            case '=':
                read();
                detectNewlines();
                return accept(ASSIGN);
            case '{':
                read();
                detectNewlines();
                return accept(LCURLY);
            case '}':
                read();
                return accept(RCURLY);
            case ':':
                read();
                detectNewlines();
                return accept(COLON);
            case '.':
                read();
                return accept(DOT);
            case '|':
                read();
                detectNewlines();
                return accept(PIPE);
        }
        if (isWhitespace(peek())) {
            if (peek() == '\r' || peek() == '\n') {
                read();
                if (peek() == '\n') {
                    read();
                }
                leaveState();
                return accept(NEWLINE);
            }
            read();
            return keepGoing();
        } else if (isIdentifier(peek())) {
            return scanIdentifier();
        } else {
            return error();
        }
    }

    private Action scanDefault() {
        if (peek() == '/') {
            if (lookAhead(1) == '*') {
                read(2);
                while (true) {
                    read();
                    if (peek() == '*') {
                        read();
                        if (peek() == '/') {
                            read();
                            break;
                        }
                    } else if (peek() == -1) {
                        throw unterminatedComment();
                    }
                }
                return keepGoing();
            } else if (lookAhead(1) == '/') {
                read(2);
                while (peek() != '\n') {
                    read();
                }
                return keepGoing();
            }
        }
        switch (peek()) {
            case '\\':
                read();
                if (lookAhead(1) == '\n' || lookAhead(1) == '\r') {
                    read();
                    detectNewlines();
                    return keepGoing();
                } else {
                    return error();
                }
            case ',':
                read();
                detectNewlines();
                return accept(COMMA);
            case '(':
                read();
                if (peek() == ')') {
                    read();
                    return accept(UNIT);
                } else if (expectQuotedIdentifier()) {
                    enterState(State.QUOTED_IDENTIFIER);
                    return keepGoing();
                } else {
                    return detectFunctionParen();
                }
            case ')':
                read();
                detectSuffix();
                return accept(RPAREN);
            case '{':
                read();
                bracesUp();
                return detectFunctionMultiline();
            case '}':
                read();
                bracesDown();
                if (endOfInterpolation()) {
                    return accept(RINTERPOLATE);
                } else {
                    detectSuffix();
                    return accept(RCURLY);
                }
            case ']':
                read();
                detectSuffix();
                return accept(RSQUARE);
            case '[':
                read();
                detectNewlines();
                return accept(LSQUARE);
            case '.':
                read();
                if (peek() == '.') {
                    read();
                    if (peek() == '.') {
                        read();
                    }
                    return accept(IDENTIFIER, text());
                } else {
                    detectNewlines();
                    return accept(DOT);
                }
            case '`':
                enterState(State.QUOTED_OPERATOR);
                read();
                return keepGoing();
            case ':':
                return scanColon();
            case ';':
                read();
                detectNewlines();
                return accept(SEMICOLON);
        }
        if (isWhitespace(peek())) {
            if (peek() == '\r' || peek() == '\n') {
                read();
                if (peek() == '\n') {
                    read();
                }
                return accept(NEWLINE);
            }
            read();
            return keepGoing();
        } else if (isDigit(peek())) {
            return scanNumber();
        } else if (peek() == '\'') {
            return scanSingleQuote();
        } else if (peek() == '"') {
            return scanDoubleQuote();
        } else if (isIdentifier(peek())) {
            LookAhead lookAhead = new LookAhead();
            if (peek() == 'c') {
                Action action = scanCharacter();
                if (action == Action.KEEP_GOING) {
                    lookAhead.restore();
                } else {
                    return action;
                }
            } else if (peek() == '?' && lookAhead(1) == '(') {
                read(2);
                return accept(LPATTERN);
            }
            return scanIdentifier();
        }
        return error();
    }

    private Action scanDoubleQuote() {
        require('"');
        if (peek() == '"' && lookAhead(1) == '"') {
            read(2);
            if (peek() == '\n') {
                read();
            }
            enterState(State.HEREDOC);
            string.setLength(0);
            return accept(TRIPLE_DQUOTE);
        } else {
            enterState(State.INTERPOLATION);
            string.setLength(0);
            return accept(DQUOTE);
        }
    }

    private Action scanEmbrace() {
        if (peek() == ':') {
            read();
            return accept(COLON);
        } else if (peek() == '.') {
            read();
            return accept(DOT);
        } else if (isIdentifier(peek())) {
            if (peek() == '-' && lookAhead(1) == '>') {
                leaveState();
            }
            return scanIdentifier();
        } else if (isWhitespace(peek())) {
            read();
            return keepGoing();
        } else {
            return error();
        }
    }

    private boolean scanEscapeSequence() {
        switch (peek()) {
            case 'u':
                read();
                while (peek() == 'u') {
                    read();
                }
                for (int i = 0; i < 4; i++) {
                    requireHex();
                }
                return true;
            case '\\':
            case 'b':
            case 't':
            case 'n':
            case 'f':
            case 'r':
            case '"':
            case '\'':
                read();
                return true;
        }
        return false;
    }

    private Action scanFunction() {
        switch (peek()) {
            case ':':
                read();
                return accept(COLON);
            case ')':
                read();
                return accept(RPAREN);
            case '.':
                read();
                return accept(DOT);
            case '\n':
            case '\t':
            case ' ':
            case '\r':
            case '\f':
                read();
                return keepGoing();
        }
        if (isIdentifier(peek())) {
            if (peek() == '-' && lookAhead(1) == '>') {
                read(2);
                leaveState();
                detectNewlines();
                return accept(APPLIES_TO);
            } else {
                while (isIdentifier(peek())) {
                    read();
                }
                return accept(FWORD, text());
            }
        } else {
            return error();
        }
    }

    private Action scanHeredoc() {
        if (peek() == '"' && lookAhead(1) == '"' && lookAhead(2) == '"') {
            if (hasString()) {
                return string();
            } else {
                read(3);
                leaveState();
                detectSuffix();
                return accept(TRIPLE_DQUOTE);
            }
        } else if (peek() == '\\') {
            read();
            if (!scanEscapeSequence()) {
                if (peek() == '#' && lookAhead(1) == '{') {
                    read(2);
                } else if (scanOctal()) {
                    octalToHex();
                    return keepGoing();
                } else {
                    return error();
                }
            }
        } else if (peek() == '#' && lookAhead(1) == '{') {
            if (hasString()) {
                Action action = string();
                string.setLength(0);
                return action;
            } else {
                read(2);
                beginInterpolation();
                return accept(LINTERPOLATE);
            }
        } else {
            read();
        }
        string.append(text());
        return keepGoing();
    }

    private Action scanIdentifier() {
        if (isPrefix(peek()) && (isLetter(lookAhead(1)) || isIdentifierDigit(lookAhead(1)) || isOpeningSeparator(lookAhead(1)))) {
            read();
            return accept(IDENTIFIER, text());
        }
        while (isIdentifier(peek())) {
            read();
        }
        while (peek() == '\'') {
            read();
        }
        String text = text();
        if (dictionary.containsKey(text)) {
            switch (text) {
                case "is":
                    if (peek() == ' ' && expect("not")) {
                        read(4);
                    }
                    break;
                case "affix":
                    if (peek() == ' ') {
                        if (expect("right")) {
                            read(" right".length());
                            return accept(PREFIX);
                        }
                    }
                    break;
                case "infix":
                    if (peek() == ' ') {
                        if (expect("left")) {
                            read(" left".length());
                            return accept(LEFT_INFIX);
                        } else if (expect("right")) {
                            read(" right".length());
                            return accept(RIGHT_INFIX);
                        } else if (expect("none")) {
                            read(" none".length());
                            return accept(INFIX);
                        }
                    }
                    break;
                case "not":
                    if (peek() == ' ') {
                        if (expect("in")) {
                            read(" in".length());
                        }
                    }
                    break;
                case "else":
                    if (peek() == ' ') {
                        if (expect("if")) {
                            read(" if".length());
                            detectNewlines();
                            return accept(ELSE_IF);
                        } else if (expect("unless")) {
                            read(" unless".length());
                            detectNewlines();
                            return accept(ELSE_UNLESS);
                        }
                    }
                    break;
                case "embrace":
                    enterState(State.EMBRACE);
                    break;
            }
            if (newlineWords.contains(text)) {
                detectNewlines();
            }
            if (dictionary.get(text) == DATA) {
                enterState(State.DATA_DECLARATION);
            }
            return accept(dictionary.get(text));
        }
        if (symbolPattern.matcher(text).find()) {
            detectNewlines();
        } else {
            detectSuffix();
        }
        return accept(IDENTIFIER, text);
    }

    private Action scanInterpolation() {
        if (peek() == '"') {
            if (hasString()) {
                return string();
            } else {
                read();
                leaveState();
                detectSuffix();
                return accept(DQUOTE);
            }
        } else if (peek() == '\n' || peek() == '\r') {
            throw unterminatedString();
        } else if (peek() == '\\') {
            read();
            if (!scanEscapeSequence()) {
                if (peek() == '#' && lookAhead(1) == '{') {
                    read(2);
                } else if (scanOctal()) {
                    octalToHex();
                    return keepGoing();
                } else {
                    return error();
                }
            }
        } else if (peek() == '#' && lookAhead(1) == '{') {
            if (hasString()) {
                return string();
            } else {
                read(2);
                beginInterpolation();
                return accept(LINTERPOLATE);
            }
        } else {
            read();
        }
        string.append(text());
        return keepGoing();
    }

    private Action scanNowdoc() {
        if (peek() == '\'' && lookAhead(1) == '\'' && lookAhead(2) == '\'') {
            if (hasString()) {
                return rawString();
            } else {
                read(3);
                leaveState();
                detectSuffix();
                return accept(TRIPLE_QUOTE);
            }
        }
        read();
        string.append(text());
        return keepGoing();
    }

    private Action scanNumber() {
        while (isDigit(peek())) {
            read();
        }
        if (peek() == '.') {
            if (isDigit(lookAhead(1))) {
                read(2);
                while (isDigit(peek())) {
                    read();
                }
                if (isIdentifier(peek())) {
                    return error();
                } else {
                    detectSuffix();
                    return accept(DOUBLE, Double.valueOf(text()));
                }
            } else {
                detectSuffix();
                return accept(INTEGER, Integer.valueOf(text()));
            }
        } else if (isIdentifier(peek())) {
            read();
            while (isIdentifier(peek())) {
                read();
            }
            detectSuffix();
            return accept(IDENTIFIER, text());
        } else {
            detectSuffix();
            return accept(INTEGER, Integer.valueOf(text()));
        }
    }

    private boolean scanOctal() {
        if (range('0', '3')) {
            range('0', '7');
            range('0', '7');
            return true;
        } else if (range('0', '7')) {
            range('0', '7');
            return true;
        }
        return false;
    }

    private Action scanQuotedIdentifier() {
        if (isQuoted(peek())) {
            while (isQuoted(peek())) {
                read();
            }
            while (peek() == '\'') {
                read();
            }
            if (peek() == ')') {
                leaveState();
                String text = text();
                if (dictionary.containsKey(text) && dictionary.get(text) != IDENTIFIER) {
                    return error();
                } else {
                    Action action = accept(QUOTED_IDENTIFIER, text);
                    read();
                    return action;
                }
            }
        }
        return error();
    }

    private Action scanQuotedOperator() {
        if (isQuoted(peek())) {
            while (isQuoted(peek())) {
                read();
            }
            while (peek() == '\'') {
                read();
            }
            if (peek() == '`') {
                leaveState();
                String text = text();
                if (dictionary.containsKey(text) && dictionary.get(text) != IDENTIFIER) {
                    return error();
                } else {
                    Action action = accept(QUOTED_OPERATOR, text);
                    read();
                    return action;
                }
            }
        }
        return error();
    }

    private Action scanSingleQuote() {
        require('\'');
        if (peek() == '\'' && lookAhead(1) == '\'') {
            read(2);
            if (peek() == '\n') {
                read();
            }
            enterState(State.NOWDOC);
            string.setLength(0);
            return accept(TRIPLE_QUOTE);
        } else {
            enterState(State.STRING);
            string.setLength(0);
            return accept(QUOTE);
        }
    }

    private Action scanString() {
        if (peek() == '\'') {
            if (hasString()) {
                return rawString();
            } else {
                read();
                leaveState();
                detectSuffix();
                return accept(QUOTE);
            }
        } else if (peek() == '\\' && lookAhead(1) == '\'') {
            read(2);
            string.append('\'');
            return keepGoing();
        } else if (peek() == '\n' || peek() == '\r') {
            throw unterminatedString();
        } else {
            read();
            string.append(text());
            return keepGoing();
        }
    }

    private Action scanSuffix() {
        if (peek() == '[') {
            read();
            return accept(LINDEX);
        } else {
            leaveState();
            return keepGoing();
        }
    }

    private Action scanTypeSignature() {
        switch (peek()) {
            case '-':
                read();
                require('>');
                detectNewlines();
                return accept(APPLIES_TO);
            case '(':
                read();
                if (peek() == ')') {
                    read();
                    return accept(UNIT);
                }
                detectNewlines();
                return accept(LPAREN);
            case ')':
                read();
                return accept(RPAREN);
            case ',':
                read();
                detectNewlines();
                return accept(COMMA);
            case '.':
                read();
                return accept(DOT);
        }
        if (isWhitespace(peek())) {
            if (peek() == '\r' || peek() == '\n') {
                read();
                if (peek() == '\n') {
                    read();
                }
                leaveState();
                return accept(NEWLINE);
            }
            read();
            return keepGoing();
        } else if (isIdentifier(peek())) {
            return scanIdentifier();
        } else {
            return error();
        }
    }

    private Action skipNewlines() {
        if (isWhitespace(peek())) {
            while (isWhitespace(peek())) {
                read();
            }
        } else {
            startPosition = markStartPosition();
            leaveState();
        }
        return keepGoing();
    }

    private State state() {
        return states.peek();
    }

    private Action string() {
        String value = string.toString();
        string.setLength(0);
        return accept(STRING, unescapeJava(value.replaceAll("\\\\u+", "\\\\u")));
    }

    private String text() {
        return new String(data, start, position - start);
    }

    private ParseException unexpected() {
        return new ParseException("Unexpected \"" + escapeJava(String.valueOf((char) peek())) + "\" in " + currentPosition());
    }

    private ParseException unterminatedComment() {
        throw new ParseException("Unterminated comment in " + currentPosition());
    }

    private ParseException unterminatedString() {
        throw new ParseException("Unterminated string in " + currentPosition());
    }

    private enum Action {
        ERROR,
        ACCEPT,
        KEEP_GOING,
    }

    private enum State {
        DEFAULT,
        STRING,
        NOWDOC,
        HEREDOC,
        INTERPOLATION,
        SUFFIX,
        FUNCTION,
        SKIP_NEWLINES,
        EMBRACE,
        QUOTED_IDENTIFIER,
        QUOTED_OPERATOR,
        DATA_DECLARATION,
        TYPE_SIGNATURE,
    }

    private class LookAhead implements AutoCloseable {

        private final int startPosition;
        private final int startLine;
        private final int startColumn;

        public LookAhead() {
            startPosition = position;
            startLine = line;
            startColumn = column;
        }

        @Override
        public void close() {
            position = startPosition;
            line = startLine;
            column = startColumn;
        }

        public void restore() {
            close();
        }
    }
}
