package snacks.lang.ast;

import snacks.lang.Type;

public abstract class AstNode {

    public void generate(Generator generator) {
        throw new UnsupportedOperationException("Cannot reference " + getClass().getSimpleName());
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
