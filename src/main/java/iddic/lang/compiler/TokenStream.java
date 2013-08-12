package iddic.lang.compiler;

import java.util.ArrayList;
import java.util.List;

public class TokenStream implements SourceStream {

    private final TokenSource source;
    private final List<Token> tokens;
    private int position;
    private boolean eof;

    public TokenStream(TokenSource source) {
        this.source = source;
        this.tokens = new ArrayList<>(1024);
    }

    @Override
    public void close() {
        source.close();
    }

    @Override
    public void consume() {
        hasMore();
        if (eof) {
            throw new IllegalStateException("Cannot consume EOF");
        } else {
            position++;
        }
    }

    public Token empty(int kind) {
        return source.empty(kind);
    }

    @Override
    public int lookAhead(int offset) {
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

    @Override
    public int peek() {
        return peekToken().getKind();
    }

    @Override
    public Position position() {
        return peekToken().getStart();
    }

    private void hasMore() {
        if (!eof && position >= size()) {
            Token token = source.nextToken();
            tokens.add(token);
            eof = (token.getKind() == EOF);
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
