package snacks.lang.compiler.ast;

import java.util.Objects;
import snacks.lang.SnacksException;

public class VariableLocator implements Locator {

    private final String name;

    public VariableLocator(String name) {
        this.name = name;
    }

    @Override
    public void accept(AstVisitor visitor) throws SnacksException {
        visitor.visitVariableLocator(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof VariableLocator && Objects.equals(name, ((VariableLocator) o).name);
    }

    public String getName() {
        return name;
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
