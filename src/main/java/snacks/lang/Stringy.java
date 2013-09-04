package snacks.lang;

public class Stringy {

    private static Stringy instance;

    public static Stringy instance() {
        if (instance == null) {
            instance = new Stringy();
        }
        return instance;
    }

    public String apply(Object argument) {
        return argument.toString();
    }
}
