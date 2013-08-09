package iddic.lang.syntax;

public class Apply implements Expression {

    private final Expression function;
    private final Expression argument;

    public Apply(Expression function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public String toString() {
        return "(" + function + " " + argument + ")";
    }
}
