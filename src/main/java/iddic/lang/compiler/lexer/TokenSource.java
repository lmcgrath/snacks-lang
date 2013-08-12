package iddic.lang.compiler.lexer;

import static iddic.lang.compiler.lexer.Terminals.*;
import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class TokenSource implements AutoCloseable {

    private static final Map<String, Integer> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("True", BOOL);
        keywords.put("False", BOOL);
        keywords.put("Nothing", NOTHING);
    }

    private static boolean isBreak(int c) {
        return isWhitespace(c) || c == '(' || c == ')' || c == SourceStream.EOF;
    }

    private static boolean isIdentifierPart(int c) {
        return c == '$'
            || c == '%'
            || c == '&'
            || c == '*'
            || c == '+'
            || c == '-'
            || c == '/'
            || c == ':'
            || c == '<'
            || c == '='
            || c == '>'
            || c == '|'
            || c == '~'
            || c == '!'
            || c == '?';
    }

    private static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\f' || c == '\r' || c == '\n';
    }

    private final TextStream input;
    private final Deque<State> states;
    private Position start;

    public TokenSource(TextStream input) {
        this.input = input;
        this.states = new ArrayDeque<>();
        this.states.push(State.DEFAULT);
    }

    @Override
    public void close() {
        input.close();
    }

    public Token empty(int kind) {
        return new Token(kind, new Segment("", input.position(), input.position()), null);
    }

    public Token nextToken() {
        skipWhitespace();
        start = input.position();
        if (expect(SourceStream.EOF)) {
            return token(SourceStream.EOF);
        }
        switch (states.peek()) {
            case DEFAULT: return matchDefault();
            case STRING: return matchString();
            default: throw new IllegalStateException();
        }
    }

    private void consume() {
        input.consume();
    }

    private void enterState(State state) {
        states.push(state);
    }

    private boolean expect(int c) {
        return peek() == c;
    }

    private boolean inRange(int c, int from, int to) {
        return c >= from && c <= to;
    }

    private void leaveState() {
        states.pop();
    }

    private boolean match(int c) {
        if (expect(c)) {
            consume();
            return true;
        } else {
            return false;
        }
    }

    private Token matchDefault() {
        if (match('(')) {
            return token(LPAREN);
        } else if (match(')')) {
            return token(RPAREN);
        } else if (match('"')) {
            enterState(State.STRING);
            return token(DQUOTE);
        } else if (isJavaIdentifierStart(peek()) || isIdentifierPart(peek())) {
            return matchId();
        } else if (isDigit(peek())) {
            return matchNumber();
        } else {
            throw new IllegalStateException();
        }
    }

    private void matchEscapeSequence() {
        if (!matchOne('r', 'n', 't', 'f', '\\', '"', '\'')) {
            if (match('u')) {
                while (expect('u')) {
                    consume();
                }
                for (int i = 0; i < 4; i++) {
                    requireHex();
                }
            } else {
                throw unexpectedInput("Illegal escape sequence");
            }
        }
    }

    private Token matchId() {
        while (isIdentifierPart(peek()) || isJavaIdentifierPart(peek())) {
            consume();
        }
        if (isBreak(peek())) {
            String text = text();
            if (keywords.containsKey(text)) {
                return token(keywords.get(text));
            } else {
                return token(ID);
            }
        } else {
            throw unexpectedInput();
        }
    }

    private Token matchNumber() {
        while (isDigit(peek())) {
            consume();
        }
        if (isBreak(peek())) {
            return token(INT, parseInt(text()));
        } else if (expect('.')) {
            if (isDigit(input.lookAhead(2))) {
                consume();
                if (isDigit(peek())) {
                    while (isDigit(peek())) {
                        consume();
                    }
                    if (isBreak(peek())) {
                        return token(DOUBLE, parseDouble(text()));
                    }
                }
            }
        }
        throw unexpectedInput();
    }

    private boolean matchOne(int... cs) {
        for (int c : cs) {
            if (match(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchRange(int from, int to) {
        if (inRange(peek(), from, to)) {
            consume();
            return true;
        } else {
            return false;
        }
    }

    private Token matchString() {
        while (true) {
            if (match('"')) {
                leaveState();
                return token(DQUOTE);
            } else if (match('\\')) {
                matchEscapeSequence();
            } else if (expect('\r') || expect('\n')) {
                throw unexpectedInput();
            } else {
                consume();
            }
            if (expect('"')) {
                return token(STRING, unescapeJava(text().replaceAll("\\\\u+", "\\\\u")));
            }
        }
    }

    private int peek() {
        return input.peek();
    }

    private void requireHex() {
        if (!matchRange('0', '9') && !matchRange('a', 'f') && !matchRange('A', 'F')) {
            throw unexpectedInput();
        }
    }

    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            input.consume();
        }
    }

    private String text() {
        return input.getSegment(start).getValue();
    }

    private Token token(int kind) {
        Segment segment = input.getSegment(start);
        return new Token(kind, segment, segment.getValue());
    }

    private Token token(int kind, Object value) {
        return new Token(kind, input.getSegment(start), value);
    }

    private ScanException unexpectedInput() {
        return new ScanException(
            input.position() + ": Unexpected '" + escapeJava(String.valueOf((char) peek())) + "' in " + states.peek()
        );
    }

    private ScanException unexpectedInput(String message) {
        return new ScanException(input.position() + ": " + message + " in " + states.peek());
    }

    private enum State {
        DEFAULT,
        STRING,
    }
}
