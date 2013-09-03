package snacks.lang;

public class Unary$Plus {

    private static Unary$Plus instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Unary$Plus();
        }
        return instance;
    }

    public Object apply(Integer argument) {
        return +argument;
    }

    public Object apply(Double argument) {
        return +argument;
    }
}
