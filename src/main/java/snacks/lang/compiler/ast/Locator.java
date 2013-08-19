package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Locator {

    private final String module;
    private final String name;

    public Locator(String module, String name) {
        this.module = module;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Locator) {
            Locator other = (Locator) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name);
    }

    @Override
    public String toString() {
        return module + "#" + name;
    }
}
