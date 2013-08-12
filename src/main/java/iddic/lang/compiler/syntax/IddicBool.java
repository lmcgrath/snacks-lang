package iddic.lang.compiler.syntax;

import java.util.Objects;

public class IddicBool implements Expression {

    public static final IddicBool TRUE = new IddicBool(true);
    public static final IddicBool FALSE = new IddicBool(false);

    private final boolean value;

    private IddicBool(boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IddicBool && value == ((IddicBool) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value ? "True" : "False";
    }
}
