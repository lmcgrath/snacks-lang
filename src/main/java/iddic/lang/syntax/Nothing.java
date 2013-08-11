package iddic.lang.syntax;

import iddic.lang.IddicException;

public class Nothing implements Expression {

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
        return "Nothing";
    }
}
