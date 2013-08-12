package iddic.lang.compiler.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Apply implements Expression {

    private final Expression function;
    private final List<Expression> arguments;

    public Apply(Expression function, Collection<Expression> arguments) {
        this.function = function;
        this.arguments = new ArrayList<>(arguments);
    }

    @Override
    public String toString() {
        return "(" + function + " " + arguments + ")";
    }
}
