package iddic.lang.compiler;

import static iddic.lang.compiler.CharStream.EOF;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;

import iddic.lang.syntax.*;

public class Parser {

    private static final char[] symbols = new char[] { '+', '-', '*', '/' };
    private final CharStream input;

    public Parser(CharStream input) {
        this.input = input;
    }

    public Expression parse() throws ParseException {
        if (peek() == EOF) {
            return new Nothing();
        } else {
            return requireList();
        }
    }

    private void consume() {
        input.consume();
    }

    private boolean expecting(char c) {
        return peek() == c;
    }

    private boolean expectingAtom() {
        skipWhitespace();
        return !expecting(')');
    }

    private boolean expectingDigit() {
        return peek() >= '0' && peek() <= '9';
    }

    private boolean expectingDouble() {
        return peek() == '.' && lookAhead(0) >= '0' && lookAhead(0) <= '9';
    }

    private boolean expectingId() {
        return isJavaIdentifierStart(peek());
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

    private boolean expectingSymbol() {
        for (char c : symbols) {
            if (expecting(c)) {
                return true;
            }
        }
        return false;
    }

    private String getText(int start) {
        return input.getText(start);
    }

    private int lookAhead(int offset) {
        return input.lookAhead(offset);
    }

    private int peek() {
        return input.peek();
    }

    private int position() {
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
        } else if (expectingSymbol()) {
            expression = requireSymbol();
        } else if (expecting('(')) {
            expression = requireList();
        } else {
            throw unexpectedInput("expecting identifier or number");
        }
        return expression;
    }

    private Expression requireId() throws ParseException {
        int start = position();
        if (isJavaIdentifierStart(peek())) {
            consume();
            while (isJavaIdentifierPart(peek())) {
                consume();
            }
            return new Identifier(getText(start));
        } else {
            throw unexpectedInput("expecting letter");
        }
    }

    private Expression requireList() throws ParseException {
        skipWhitespace();
        require('(');
        Expression expression = requireAtom();
        while (expectingAtom()) {
            expression = new Apply(expression, requireAtom());
        }
        skipWhitespace();
        require(')');
        return expression;
    }

    private Expression requireNumber() throws ParseException {
        int start = position();
        if (expectingDigit()) {
            while (expectingDigit()) {
                consume();
            }
            if (expectingDouble()) {
                consume();
                while (expectingDigit()) {
                    consume();
                }
                return new DoubleLiteral(Double.parseDouble(getText(start)));
            } else {
                return new IntegerLiteral(Integer.parseInt(getText(start)));
            }
        } else if (expectingDouble()) {
            consume();
            while (expectingDigit()) {
                consume();
            }
            return new DoubleLiteral(Double.parseDouble(getText(start)));
        } else {
            throw unexpectedInput("expecting digits");
        }
    }

    private Expression requireSymbol() {
        int start = position();
        consume();
        return new Identifier(getText(start));
    }

    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            consume();
        }
    }

    private ParseException unexpectedInput(String alternative) throws ParseException {
        int peek = peek();
        String input;
        if (peek == EOF) {
            input = "end-of-file";
        } else {
            input = "'" + (char) peek + "'";
        }
        throw new ParseException("Unexpected " + input + "; " + alternative);
    }
}
