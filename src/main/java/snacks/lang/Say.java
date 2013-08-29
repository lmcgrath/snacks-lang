package snacks.lang;

import static java.lang.System.out;

public class Say {

    private static Say instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Say();
        }
        return instance;
    }

    public Object apply(Boolean value) {
        out.println(value.booleanValue());
        return null;
    }

    public Object apply(Double value) {
        out.println(value.doubleValue());
        return null;
    }

    public Object apply(Integer value) {
        out.println(value.intValue());
        return null;
    }

    public Object apply(String value) {
        out.println(value);
        return null;
    }

    public Object apply(Object value) {
        out.println(value);
        return null;
    }
}
