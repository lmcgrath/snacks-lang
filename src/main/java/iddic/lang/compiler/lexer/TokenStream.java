package iddic.lang.compiler.lexer;

import static iddic.lang.compiler.lexer.Terminal.EOF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenStream {

    private final TokenSource source;
    private final List<Token> tokens;
    private int position;
    private boolean eof;

    public TokenStream(TokenSource source) {
        this.source = source;
        this.tokens = new ArrayList<>(1024);
    }

    public void close() {
        source.close();
    }

    public void consume() {
        hasMore();
        if (eof) {
            throw new IllegalStateException("Cannot consume EOF");
        } else {
            position++;
        }
    }

    public Token empty(Terminal kind) {
        return new Token(kind, null);
    }

    public Terminal lookAhead(int offset) {
        hasMore();
        if (offset < 0) {
            offset++;
        }
        int adjustedOffset = position + offset - 1;
        if (adjustedOffset >= 0 && adjustedOffset < size()) {
            return tokens.get(adjustedOffset).getKind();
        } else {
            return EOF;
        }
    }

    public Token nextToken() {
        if (eof) {
            return tokens.get(size() - 1);
        } else {
            Token token = tokens.get(position);
            consume();
            return token;
        }
    }

    public Terminal peek() {
        return peekToken().getKind();
    }

    private void hasMore() {
        if (!eof && position >= size()) {
            try {
                Token token = source.nextToken();
                tokens.add(token);
                eof = (token.getKind() == EOF);
            } catch (IOException exception) {
                throw new ScanException(exception);
            }
        }
    }

    private Token peekToken() {
        hasMore();
        return tokens.get(position);
    }

    private int size() {
        return tokens.size();
    }
}
