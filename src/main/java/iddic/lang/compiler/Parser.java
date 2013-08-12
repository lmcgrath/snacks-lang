package iddic.lang.compiler;

import static iddic.lang.compiler.StringStream.EOF;
import static iddic.lang.compiler.Terminals.*;

import java.util.ArrayList;
import java.util.List;
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

    public Expression parse() throws ParseException {
        if (expect(EOF)) {
            return Nothing.INSTANCE;
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

    private Expression requireAtom() throws ParseException {
        if (expect(ID)) {
            return new Identifier((String) nextToken().getValue());
        } else if (expect(BOOL)) {
            return "True".equals(nextToken().getValue()) ? IddicBool.TRUE : IddicBool.FALSE;
        } else if (expect(NOTHING)) {
            return Nothing.INSTANCE;
        } else if (expect(INT)) {
            return new IddicInteger((Integer) nextToken().getValue());
        } else if (expect(DOUBLE)) {
            return new IddicDouble((Double) nextToken().getValue());
        } else if (expect(DOUBLE_QUOTE)) {
            require(DOUBLE_QUOTE);
            Expression expression = new IddicString((String) require(STRING).getValue());
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
