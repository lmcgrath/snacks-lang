package snacks.lang;

public class SnackDefinition {

    private final String javaName;
    private final byte[] bytes;

    public SnackDefinition(String javaName, byte[] bytes) {
        this.javaName = javaName;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getJavaName() {
        return javaName;
    }

    @Override
    public String toString() {
        return "(SnackDefinition '" + javaName + "')";
    }
}
