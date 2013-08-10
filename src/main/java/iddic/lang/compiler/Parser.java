package iddic.lang.compiler;

import static com.google.common.primitives.Chars.asList;
import static iddic.lang.compiler.StringReader.EOF;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;

import java.util.List;
import iddic.lang.syntax.*;

public class Parser implements AutoCloseable {

    private static final List<Character> SYMBOL_CHARS = asList(
        '+', '-', '*', '/', '<', '=', '>', '@', '!', '$', '^', '&', '|', '?', '.', ':', '~', '_'
    );

    private final InputReader input;

    public Parser(InputReader input) {
        this.input = input;
    }

    @Override
    public void close() {
        input.close();
    }

    public Expression parse() throws ParseException {
        skipWhitespace();
        if (peek() == EOF) {
            return new Nothing();
        } else {
            return requirePhrase();
        }
    }

    private void consume() {
        input.consume();
    }

    private void consume(int length) {
        for (int i = 0; i < length; i++) {
            consume();
        }
    }

    private void ensureNoAtoms() throws ParseException {
        if (expectingAtom() && peek() != '(') {
            throw unexpected();
        }
    }

    private void ensureNoNonDigits() throws ParseException {
        if (expectingId()) {
            throw unexpected();
        } else if (expectingString()) {
            throw unexpected("string");
        }
    }

    private void ensureNoStrings() throws ParseException {
        if (expectingString()) {
            throw unexpected("string");
        }
    }

    private boolean expecting(char c) {
        return peek() == c;
    }

    private boolean expectingAtom() {
        return !expecting(')');
    }

    private boolean expectingDigit() {
        return peek() >= '0' && peek() <= '9';
    }

    private boolean expectingDouble() {
        return peek() == '.' && lookAhead(0) >= '0' && lookAhead(0) <= '9';
    }

    private boolean expectingId() {
        return isId(peek());
    }

    private boolean expectingKeyword(String word) throws ParseException {
        for (int i = 0; i < word.length(); i++) {
            if (lookAhead(i + 1) != word.charAt(i)) {
                return false;
            }
        }
        return !isId(lookAhead(word.length() + 1));
    }

    private boolean expectingNumber() {
        int peek = peek();
        if (peek >= '0' && peek <= '9') {
            return true;
        } else if (peek == '.') {
            int lookAhead = lookAhead(0);
            return lookAhead >= '0' && peek <= '9';
        } else {
            return false;
        }
    }

    private boolean expectingString() {
        return peek() == '"';
    }

    private String getText(Position start) {
        return input.getSegment(start).getValue();
    }

    private boolean isId(int c) {
        return isLetter(c) || SYMBOL_CHARS.contains((char) c);
    }

    private int lookAhead(int offset) {
        return input.lookAhead(offset);
    }

    private int peek() {
        return input.peek();
    }

    private Position position() {
        return input.position();
    }

    private void require(char c) throws ParseException {
        if (peek() == c) {
            consume();
        } else {
            throw unexpectedInput("expecting '" + c + "'");
        }
    }

    private Expression requireAtom() throws ParseException {
        Expression expression;
        if (expectingId()) {
            expression = requireId();
        } else if (expectingNumber()) {
            expression = requireNumber();
        } else if (expectingString()) {
            expression = requireString();
        } else if (expecting('(')) {
            expression = requirePhrase();
        } else {
            throw unexpectedInput("expecting identifier or number");
        }
        return expression;
    }

    private Expression requireId() throws ParseException {
        return new Identifier(requireIdText());
    }

    private String requireIdText() throws ParseException {
        Position start = position();
        if (expectingId()) {
            consume();
            while (expectingId() || expectingDigit()) {
                consume();
            }
            ensureNoStrings();
            return getText(start);
        } else {
            throw unexpectedInput("expecting identifier");
        }
    }

    private void requireKeyword(String word) throws ParseException {
        for (int i = 0; i < word.length(); i++) {
            if (!expecting(word.charAt(i))) {
                throw unexpectedInput("keyword '" + word + "'");
            }
            consume();
        }
        if (isId(peek())) {
            throw unexpectedInput("keyword '" + word + "'");
        }
    }

    private Expression requireLet() throws ParseException {
        requireKeyword("let");
        skipWhitespace();
        String id = requireIdText();
        skipWhitespace();
        require('=');
        skipWhitespace();
        Expression definition = requireAtom();
        skipWhitespace();
        requireKeyword("in");
        skipWhitespace();
        Expression scope = requirePhrase();
        return new Let(id, definition, scope);
    }

    private Expression requireNumber() throws ParseException {
        if (expectingDigit() || expectingDouble()) {
            Position start = position();
            Expression expression;
            while (expectingDigit()) {
                consume();
            }
            if (expectingDouble()) {
                consume();
                while (expectingDigit()) {
                    consume();
                }
                expression = new DoubleLiteral(Double.parseDouble(getText(start)));
            } else {
                expression = new IntegerLiteral(Integer.parseInt(getText(start)));
            }
            ensureNoNonDigits();
            return expression;
        } else {
            throw unexpectedInput("expecting integer or double");
        }
    }

    private Expression requirePhrase() throws ParseException {
        require('(');
        skipWhitespace();
        Expression expression;
        if (expectingKeyword("let")) {
            expression = requireLet();
        } else {
            expression = requireAtom();
            skipWhitespace();
            while (expectingAtom()) {
                expression = new Apply(expression, requireAtom());
                skipWhitespace();
            }
        }
        skipWhitespace();
        require(')');
        return expression;
    }

    private Expression requireString() throws ParseException {
        require('"');
        Position start = position();
        while (peek() != '"') {
            consume();
        }
        Expression expression = new StringLiteral(unescapeJava(getText(start)));
        require('"');
        ensureNoAtoms();
        return expression;
    }

    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            consume();
        }
    }

    private ParseException unexpected() throws ParseException {
        throw new ParseException("Unexpected '" + (char) peek() + "' in " + position());
    }

    private ParseException unexpected(String name) throws ParseException {
        throw new ParseException("Unexpected " + name + " in " + position());
    }

    private ParseException unexpectedInput(String alternative) throws ParseException {
        int peek = peek();
        String input;
        if (peek == EOF) {
            input = "end-of-file";
        } else {
            input = "'" + (char) peek + "'";
        }
        throw new ParseException("Unexpected " + input + "; " + alternative + " in " + position());
    }
}
