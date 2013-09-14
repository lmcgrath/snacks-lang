package snacks.lang.parser;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.parser.Terminals.NAMES;

import beaver.Symbol;

public class Token extends Symbol {

    private Position position;

    public Token(short kind, Position position) {
        super(kind);
        this.position = position;
    }

    public Token(short kind, Object value, Position position) {
        super(kind, value);
        this.position = position;
    }

    @Override
    public int getEnd() {
        return makePosition(position.getEndLine(), position.getEndColumn());
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public int getStart() {
        return makePosition(position.getStartLine(), position.getStartColumn());
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        String name;
        if (getId() <= NAMES.length) {
            name = NAMES[getId()];
        } else {
            name = "UNKNOWN";
        }
        return name + "{value=\"" + escapeJava(value == null ? null : value.toString()) + "\" position=" + position + "}";
    }
}
