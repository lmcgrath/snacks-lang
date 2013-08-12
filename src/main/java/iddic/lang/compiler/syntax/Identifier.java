package iddic.lang.compiler.syntax;

import java.util.Objects;

public class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Identifier && Objects.equals(name, ((Identifier) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
