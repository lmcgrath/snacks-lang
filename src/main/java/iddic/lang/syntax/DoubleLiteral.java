package iddic.lang.syntax;

public class DoubleLiteral implements Expression {

    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
