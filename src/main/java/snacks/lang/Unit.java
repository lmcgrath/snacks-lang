package snacks.lang;

public class Unit {

    public static final Unit UNIT = new Unit();

    private Unit() {
        // intentionally empty
    }

    @Override
    public String toString() {
        return "()";
    }
}
