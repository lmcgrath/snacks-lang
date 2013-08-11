package iddic.lang.compiler;

import static iddic.lang.compiler.StringStream.EOF;
import static iddic.lang.compiler.Terminals.*;

import java.util.ArrayList;
import java.util.List;
import iddic.lang.syntax.*;

public class Parser implements AutoCloseable {

    private final TokenStream input;

    public Parser(TokenStream input) {
        this.input = input;
    }

    @Override
    public void close() {
        input.close();
    }

    public Expression parse() throws ParseException {
        if (expect(EOF)) {
            return new Nothing();
        } else {
            return requirePhrase();
        }
    }

    private boolean expect(int kind) {
        return peek() == kind;
    }

    private boolean expectAtom() {
        return peek() == ID
            || peek() == INT
            || peek() == DOUBLE
            || peek() == DOUBLE_QUOTE
            || peek() == LPAREN;
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

    private Expression requireAtom() throws ParseException {
        if (expect(ID)) {
            return new Identifier((String) nextToken().getValue());
        } else if (expect(INT)) {
            return new IntegerLiteral((Integer) nextToken().getValue());
        } else if (expect(DOUBLE)) {
            return new DoubleLiteral((Double) nextToken().getValue());
        } else if (expect(DOUBLE_QUOTE)) {
            require(DOUBLE_QUOTE);
            Expression expression = new StringLiteral((String) require(STRING).getValue());
            require(DOUBLE_QUOTE);
            return expression;
        } else if (expect(LPAREN)) {
            return requirePhrase();
        } else {
            throw unexpectedToken();
        }
    }

    private Expression requirePhrase() throws ParseException {
        require(LPAREN);
        Expression expression = requireAtom();
        if (expectAtom()) {
            List<Expression> arguments = new ArrayList<>();
            while (expectAtom()) {
                arguments.add(requireAtom());
            }
            expression = new Apply(expression, arguments);
        }
        require(RPAREN);
        return expression;
    }

    private ParseException unexpectedToken() {
        return new ParseException(position() + ": Unexpected " + nameOf(peek()));
    }
}
