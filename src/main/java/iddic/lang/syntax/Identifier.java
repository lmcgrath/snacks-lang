package iddic.lang.syntax;

import java.util.Objects;
import iddic.lang.IddicException;

public class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public Expression apply(Expression... arguments) throws IddicException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Identifier && Objects.equals(name, ((Identifier) o).name);
    }

    @Override
    public Expression evaluate() throws IddicException {
        return this;
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
