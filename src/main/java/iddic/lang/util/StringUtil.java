package iddic.lang.util;

public final class StringUtil {

    public static String asLines(String... inputs) {
        StringBuilder builder = new StringBuilder();
        builder.append(inputs[0]);
        for (int i = 1; i < inputs.length; i++) {
            builder.append('\n');
            builder.append(inputs[i]);
        }
        return builder.toString();
    }

    public static String stringify(Object o, Object... otherObjects) {
        StringBuilder builder = new StringBuilder();
        builder.append('(').append(o.getClass().getSimpleName());
        for (Object otherObject : otherObjects) {
            builder.append(' ').append(otherObject);
        }
        builder.append(')');
        return builder.toString();
    }

    private StringUtil() {
        // intentionally empty
    }
}
