package snacks.lang;

public class Say implements Applicable {

    private static Say instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Say();
        }
        return instance;
    }

    public Object apply(Object value) {
        System.out.println(value.toString());
        return null;
    }
}
