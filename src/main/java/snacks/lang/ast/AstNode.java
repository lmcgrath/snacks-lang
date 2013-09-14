package snacks.lang.ast;

import snacks.lang.Type;
import snacks.lang.util.Position;

public abstract class AstNode {

    private Position position;

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

    public boolean isInvokable() {
        return false;
    }

    public void print(AstPrinter printer) {
        // intentionally empty
    }

    public void reduce(Reducer reducer) {
        throw new UnsupportedOperationException("Cannot assign to " + getClass().getSimpleName());
    }
}
