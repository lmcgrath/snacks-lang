package iddic.lang.syntax;

public class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
