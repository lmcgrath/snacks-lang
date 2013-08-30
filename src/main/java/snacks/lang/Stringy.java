package snacks.lang;

public class Stringy {

    private static Stringy instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Stringy();
        }
        return instance;
    }

    public Object apply(Object argument) {
        return argument.toString();
    }
}
