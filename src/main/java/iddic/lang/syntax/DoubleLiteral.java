package iddic.lang.syntax;

import iddic.lang.IddicException;

public class DoubleLiteral implements Expression {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public Expression apply(Expression... arguments) throws IddicException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression evaluate() throws IddicException {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
