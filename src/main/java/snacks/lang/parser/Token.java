package snacks.lang.parser;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import beaver.Symbol;

public class Token extends Symbol {

    private final String source;

    public Token(short kind, int line, int column, int length, String source) {
        super(kind, line, column, length);
        this.source = source;
    }

    public Token(short kind, int line, int column, int length, String source, Object value) {
        super(kind, line, column, length, value);
        this.source = source;
    }

    public int getKind() {
        return super.getId();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    @Override
    public String toString() {
        return String.format(
            "(%s \"%s\" in %s: %d, %d)",
            Terminals.NAMES[getKind()],
            escapeJava(value.toString()),
            source,
            getLine(getStart()),
            getColumn(getStart())
        );
    }
}
