package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class DeclarationLocator implements Locator {

    private final String module;
    private final String name;

    public DeclarationLocator(String module, String name) {
        this.module = module;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclarationLocator) {
            DeclarationLocator other = (DeclarationLocator) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
                .isEquals();
        } else {
            return false;
        }
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
