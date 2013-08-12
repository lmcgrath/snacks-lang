package iddic.lang.compiler.syntax;

public class IddicInteger implements Expression {

    private final int value;

    public IddicInteger(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
