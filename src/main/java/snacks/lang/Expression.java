package snacks.lang;

public abstract class Expression {

    public Expression apply(Expression argument) {
        throw new UnsupportedOperationException();
    }
}
