package iddic.lang.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import iddic.lang.IddicException;

public class Apply implements Expression {

    private final Expression function;
    private final List<Expression> arguments;

    public Apply(Expression function, Collection<Expression> arguments) {
        this.function = function;
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public Expression apply(Expression... arguments) throws IddicException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Expression evaluate() throws IddicException {
        return function.apply(arguments.toArray(new Expression[arguments.size()]));
    }

    @Override
    public String toString() {
        return "(" + function + " " + arguments + ")";
    }
}
