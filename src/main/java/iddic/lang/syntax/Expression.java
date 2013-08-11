package iddic.lang.syntax;

import iddic.lang.IddicException;

public interface Expression {

    Expression apply(Expression... arguments) throws IddicException;

    Expression evaluate() throws IddicException;
}
