package snacks.lang.ast;

import snacks.lang.Type;
import snacks.lang.util.Position;

public abstract class AstNode {

    private Position position;

    @Override
    public abstract boolean equals(Object o);

    public void generate(Generator generator) {
        throw new UnsupportedOperationException("Cannot reference " + getClass().getSimpleName());
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract Type getType();

    @Override
    public abstract int hashCode();

    public boolean isInvokable() {
        return false;
    }

    public abstract void print(AstPrinter printer);

    public void reduce(Reducer reducer) {
        throw new UnsupportedOperationException("Cannot assign to " + getClass().getSimpleName());
    }

    @Override
    public abstract String toString();
}
