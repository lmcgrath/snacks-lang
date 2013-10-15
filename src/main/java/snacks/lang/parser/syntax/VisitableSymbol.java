package snacks.lang.parser.syntax;

import beaver.Symbol;
import snacks.lang.util.Position;

public abstract class VisitableSymbol extends Symbol {

    private Position position;

    public abstract void accept(SyntaxVisitor visitor);

    @Override
    public abstract boolean equals(Object o);

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
