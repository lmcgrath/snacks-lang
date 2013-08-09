package iddic.lang.syntax;

public class IntegerLiteral implements Expression {

    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
