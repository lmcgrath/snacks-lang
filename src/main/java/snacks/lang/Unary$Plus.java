package snacks.lang;

public class Unary$Plus {

    private static Unary$Plus instance;

    public static Unary$Plus instance() {
        if (instance == null) {
            instance = new Unary$Plus();
        }
        return instance;
    }

    public Integer apply(Integer argument) {
        return +argument;
    }

    public Double apply(Double argument) {
        return +argument;
    }
}
