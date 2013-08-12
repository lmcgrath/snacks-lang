package iddic.lang.compiler;

import static iddic.lang.compiler.StringStream.EOF;
import static iddic.lang.compiler.Terminals.*;

import iddic.lang.compiler.syntax.*;

public class Parser implements AutoCloseable {

    private final TokenStream input;

    public Parser(TokenStream input) {
        this.input = input;
    }

    @Override
    public void close() {
        input.close();
    }

    public SyntaxNode parse() throws ParseException {
        if (expect(EOF)) {
            return new NothingNode(empty(NOTHING));
        } else {
            return requirePhrase();
        }
    }

    private boolean expect(int kind) {
        return peek() == kind;
    }

    private boolean expectAtom() {
        int t = peek();
        return t == ID
            || t == BOOL
            || t == NOTHING
            || t == INT
            || t == DOUBLE
            || t == DOUBLE_QUOTE
            || t == LPAREN;
    }

    private Token nextToken() {
        return input.nextToken();
    }

    private int peek() {
        return input.peek();
    }

    private Position position() {
        return input.position();
    }

    private Token require(int kind) throws ParseException {
        if (expect(kind)) {
            return nextToken();
        } else {
            throw unexpectedToken();
        }
    }

    private SyntaxNode requireAtom() throws ParseException {
        if (expect(ID)) {
            return new IdentifierNode(nextToken());
        } else if (expect(BOOL)) {
            return new BooleanNode(nextToken());
        } else if (expect(NOTHING)) {
            return new NothingNode(nextToken());
        } else if (expect(INT)) {
            return new IntegerNode(nextToken());
        } else if (expect(DOUBLE)) {
            return new DoubleNode(nextToken());
        } else if (expect(DOUBLE_QUOTE)) {
            require(DOUBLE_QUOTE);
            SyntaxNode node = new StringNode((expect(STRING) ? require(STRING) : empty(STRING)));
            require(DOUBLE_QUOTE);
            return node;
        } else if (expect(LPAREN)) {
            return requirePhrase();
        } else {
            throw unexpectedToken();
        }
    }

    private Token empty(int kind) {
        return input.empty(kind);
    }

    private SyntaxNode requirePhrase() throws ParseException {
        require(LPAREN);
        SyntaxNode node = requireAtom();
        while (expectAtom()) {
            node = new ApplyNode(node, requireAtom());
        }
        require(RPAREN);
        return node;
    }

    private ParseException unexpectedToken() {
        return new ParseException(position() + ": Unexpected " + nameOf(peek()));
    }
}
