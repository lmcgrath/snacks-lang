package snacks.lang;

public class Unary$Minus {

    private static Unary$Minus instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Unary$Minus();
        }
        return instance;
    }

    public Object apply(Integer argument) {
        return -argument;
    }

    public Object apply(Double argument) {
        return -argument;
    }
}
