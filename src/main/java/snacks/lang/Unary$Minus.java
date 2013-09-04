package snacks.lang;

public class Unary$Minus {

    private static Unary$Minus instance;

    public static Unary$Minus instance() {
        if (instance == null) {
            instance = new Unary$Minus();
        }
        return instance;
    }

    public Integer apply(Integer argument) {
        return -argument;
    }

    public Double apply(Double argument) {
        return -argument;
    }
}
