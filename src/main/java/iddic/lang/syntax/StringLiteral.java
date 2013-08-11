package iddic.lang.syntax;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import iddic.lang.IddicException;

public class StringLiteral implements Expression {

    private final String value;

    public StringLiteral(String value) {
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
        return '"' + escapeJava(value) + '"';
    }
}
