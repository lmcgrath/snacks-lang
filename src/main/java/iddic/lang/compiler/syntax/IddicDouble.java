package iddic.lang.compiler.syntax;

public class IddicDouble implements Expression {

    private final double value;

    public IddicDouble(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
