package iddic.lang.compiler.parser;

import static iddic.lang.compiler.lexer.Terminal.*;

import iddic.lang.compiler.lexer.Terminal;
import iddic.lang.compiler.lexer.Token;
import iddic.lang.compiler.lexer.TokenStream;
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

    private boolean expect(Terminal kind) {
        return peek() == kind;
    }

    private boolean expectAtom() {
        Terminal t = peek();
        return t == ID
            || t == BOOL
            || t == NOTHING
            || t == INTEGER
            || t == DOUBLE
            || t == DQUOTE
            || t == LPAREN;
    }

    private Token nextToken() {
        return input.nextToken();
    }

    private Terminal peek() {
        return input.peek();
    }

    private Token require(Terminal kind) throws ParseException {
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
        } else if (expect(INTEGER)) {
            return new IntegerNode(nextToken());
        } else if (expect(DOUBLE)) {
            return new DoubleNode(nextToken());
        } else if (expect(DQUOTE)) {
            require(DQUOTE);
            SyntaxNode node = new StringNode((expect(STRING) ? require(STRING) : empty(STRING)));
            require(DQUOTE);
            return node;
        } else if (expect(LPAREN)) {
            return requirePhrase();
        } else {
            throw unexpectedToken();
        }
    }

    private Token empty(Terminal kind) {
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
        return new ParseException("Unexpected " + peek());
    }
}
