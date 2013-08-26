package snacks.lang;

public class SnacksString extends Expression {

    private final String value;

    public SnacksString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
