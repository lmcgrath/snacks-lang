package snacks.lang.compiler;

import snacks.lang.Type;

public class SnackDefinition {

    private final String snackName;
    private final String javaName;
    private final Type type;
    private final byte[] bytes;

    public SnackDefinition(String snackName, String javaName, Type type, byte[] bytes) {
        this.snackName = snackName;
        this.javaName = javaName;
        this.type = type;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getJavaName() {
        return javaName;
    }

    public String getSnackName() {
        return snackName;
    }

    public Type getType() {
        return type;
    }
}
