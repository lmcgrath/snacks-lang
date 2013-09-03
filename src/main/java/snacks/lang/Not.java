package snacks.lang;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Not {

    private static Not instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Not();
        }
        return instance;
    }

    public Object apply(Boolean argument) {
        return argument ? FALSE : TRUE;
    }
}
