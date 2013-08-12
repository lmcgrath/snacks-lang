package iddic.lang.compiler.syntax;

public class Nothing implements Expression {

    public static final Nothing INSTANCE = new Nothing();

    private Nothing() {
        // intentionally empty
    }

    @Override
    public String toString() {
        return "Nothing";
    }
}
